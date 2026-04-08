import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.testing.GroupTestEventReporter;
import org.gradle.api.tasks.testing.TestEventReporter;
import org.gradle.api.tasks.testing.TestEventReporterFactory;
import org.gradle.api.tasks.testing.TestOutputEvent;

import javax.inject.Inject;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task that parses JUnit XML reports produced by vitest (or any JUnit-compatible runner)
 * and generates an HTML test report using Gradle's public Test Event Reporting API.
 *
 * <p>Usage:
 * <pre>
 * tasks.register('testReport', JUnitXmlToHtmlConverterTask) {
 *     group = 'verification'
 *     from layout.buildDirectory.dir('test-results/test')
 *     into rootProject.layout.projectDirectory.dir('pages/html/' + project.name + '/test')
 * }
 * </pre>
 */
public abstract class JUnitXmlToHtmlConverterTask extends DefaultTask {

    /**
     * Directory containing JUnit XML report files ({@code *.xml}).
     * Set via the {@code from} clause.
     */
    @InputDirectory
    public abstract DirectoryProperty getFrom();

    /**
     * Directory the HTML test report is written to.
     * Set via the {@code into} clause.
     */
    @OutputDirectory
    public abstract DirectoryProperty getInto();

    @Inject
    protected abstract TestEventReporterFactory getTestEventReporterFactory();

    /** Accepts a {@code Provider<Directory>} as the XML input directory (e.g. from {@code layout.buildDirectory.dir(...)}). */
    public JUnitXmlToHtmlConverterTask from(Provider<Directory> provider) {
        getFrom().set(provider);
        return this;
    }

    /** Accepts a {@code Directory} as the XML input directory (e.g. from {@code layout.projectDirectory.dir(...)}). */
    public JUnitXmlToHtmlConverterTask from(Directory directory) {
        getFrom().set(directory);
        return this;
    }

    /** Accepts a {@code Provider<Directory>} as the HTML output directory. */
    public JUnitXmlToHtmlConverterTask into(Provider<Directory> provider) {
        getInto().set(provider);
        return this;
    }

    /** Accepts a {@code Directory} as the HTML output directory (e.g. from {@code layout.projectDirectory.dir(...)}). */
    public JUnitXmlToHtmlConverterTask into(Directory directory) {
        getInto().set(directory);
        return this;
    }

    @TaskAction
    public void execute() {
        var xmlDir = getFrom().get().getAsFile();
        var xmlFiles = findXmlFiles(xmlDir);
        if (xmlFiles.isEmpty()) {
            throw new GradleException(
                    "No JUnit XML report files found in: " + relativePath(xmlDir)
                    + ". Run the test task first to generate XML results.");
        }

        var allSuites = parseXmlFiles(xmlFiles);

        var htmlDir = getInto().get();
        if (htmlDir.getAsFile().mkdirs()) {
            getLogger().info("{}: created output directory {}", getName(), relativePath(htmlDir.getAsFile()));
        }

        // Use the task's temporary directory for the intermediate binary results.
        // It is derived from the task name so each task instance has its own location.
        var binaryDirFile = new File(getTemporaryDir(), "binary");
        if (binaryDirFile.mkdirs()) {
            getLogger().info("{}: created binary results directory {}", getName(), relativePath(binaryDirFile));
        }
        var binaryDirProp = getProject().getObjects().directoryProperty();
        binaryDirProp.set(binaryDirFile);
        var binaryDir = binaryDirProp.get();

        generateReport(allSuites, binaryDir, htmlDir);

        getLogger().lifecycle("HTML test report written to {}", relativePath(htmlDir.getAsFile()));
    }

    // -------------------------------------------------------------------------
    // XML discovery and parsing
    // -------------------------------------------------------------------------

    private List<File> findXmlFiles(File dir) {
        var result = new ArrayList<File>();
        collectXmlFiles(dir, result);
        result.sort(File::compareTo);
        return result;
    }

    private void collectXmlFiles(File dir, List<File> result) {
        var entries = dir.listFiles();
        if (entries == null) {
            return;
        }
        Arrays.sort(entries);
        for (File entry : entries) {
            if (entry.isDirectory()) {
                collectXmlFiles(entry, result);
            } else if (entry.getName().endsWith(".xml")) {
                result.add(entry);
            }
        }
    }

    private List<JunitXmlTestSuite> parseXmlFiles(List<File> xmlFiles) {
        var parser = new JunitXmlParser();
        var allSuites = new ArrayList<JunitXmlTestSuite>();
        for (File xmlFile : xmlFiles) {
            getLogger().info("{}: parsing {}", getName(), relativePath(xmlFile));
            allSuites.addAll(parser.parse(xmlFile));
        }
        return allSuites;
    }

    // -------------------------------------------------------------------------
    // Test event replay
    // -------------------------------------------------------------------------

    private void generateReport(List<JunitXmlTestSuite> suites, Directory binaryDir, Directory htmlDir) {
        var rootName = getProject().getName();
        try (var root =
                getTestEventReporterFactory().createTestEventReporter(rootName, binaryDir, htmlDir)) {
            var startTime = Instant.now();
            root.started(startTime);
            for (JunitXmlTestSuite suite : suites) {
                replaySuite(root, suite, startTime);
            }
            root.succeeded(Instant.now());
        }
    }

    private void replaySuite(GroupTestEventReporter parent, JunitXmlTestSuite suite, Instant baseTime) {
        getLogger().info("{}: replaying suite '{}'", getName(), suite.name());
        try (var suiteReporter = parent.reportTestGroup(sanitizeGroupName(suite.name()))) {
            var suiteStart = baseTime;
            suiteReporter.started(suiteStart);

            if (!suite.stdout().isBlank()) {
                suiteReporter.output(suiteStart, TestOutputEvent.Destination.StdOut, suite.stdout());
            }
            if (!suite.stderr().isBlank()) {
                suiteReporter.output(suiteStart, TestOutputEvent.Destination.StdErr, suite.stderr());
            }

            for (JunitXmlTestCase testCase : suite.testCases()) {
                replayTestCase(suiteReporter, testCase, suiteStart);
            }

            var suiteDurationMs = Math.max((long) (suite.durationSeconds() * 1000), 1L);
            suiteReporter.succeeded(suiteStart.plusMillis(suiteDurationMs));
        }
    }

    private void replayTestCase(GroupTestEventReporter parent, JunitXmlTestCase testCase, Instant baseTime) {
        var name = sanitizeTestName(testCase.name());
        var displayName = buildDisplayName(testCase);
        try (var caseReporter = parent.reportTest(name, displayName)) {
            var caseStart = baseTime;
            caseReporter.started(caseStart);

            if (!testCase.stdout().isBlank()) {
                caseReporter.output(caseStart, TestOutputEvent.Destination.StdOut, testCase.stdout());
            }
            if (!testCase.stderr().isBlank()) {
                caseReporter.output(caseStart, TestOutputEvent.Destination.StdErr, testCase.stderr());
            }

            var durationMs = Math.max((long) (testCase.durationSeconds() * 1000), 1L);
            var caseEnd = caseStart.plusMillis(durationMs);

            if (testCase.skipped()) {
                caseReporter.skipped(caseEnd);
            } else if (!testCase.failures().isEmpty()) {
                logFailures(displayName, testCase.failures());
                var first = testCase.failures().get(0);
                var message = first.message().isBlank() ? first.type() : first.message();
                var additionalContent = buildAdditionalContent(testCase.failures());
                caseReporter.failed(caseEnd, message, additionalContent);
            } else {
                caseReporter.succeeded(caseEnd);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private String buildDisplayName(JunitXmlTestCase testCase) {
        var classname = testCase.classname();
        var name = testCase.name();
        if (classname != null && !classname.isBlank() && !classname.equals(name)) {
            return classname + " > " + name;
        }
        return name;
    }

    private String buildAdditionalContent(List<JunitXmlTestFailure> failures) {
        var sb = new StringBuilder();
        for (JunitXmlTestFailure failure : failures) {
            String content = failure.content().strip();
            if (!content.isBlank()) {
                sb.append(content).append(System.lineSeparator());
            }
        }
        return sb.toString().strip();
    }

    private void logFailures(String displayName, List<JunitXmlTestFailure> failures) {
        for (JunitXmlTestFailure failure : failures) {
            var message = failure.message().isBlank() ? failure.type() : failure.message();
            getLogger().info("{}: FAILED '{}' - {}", getName(), displayName, message);
            if (!failure.content().isBlank()) {
                getLogger().info("{}: {}", getName(), failure.content().strip());
            }
        }
    }

    private String relativePath(File file) {
        try {
            return getProject().getRootDir().toPath().relativize(file.toPath()).toString();
        } catch (IllegalArgumentException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * Sanitizes a suite name for use as a file-system path component.
     * Forward slashes are kept so vitest file-path suite names form a readable directory tree.
     * All other Windows-illegal characters and spaces are replaced with {@code _};
     * consecutive underscores are collapsed into one.
     */
    private static String sanitizeGroupName(String name) {
        return name.replaceAll("[\\\\:*?\"<>| ]", "_").replaceAll("_+", "_");
    }

    /**
     * Sanitizes a test-case name for use as a file-system path component.
     * All path separators, Windows-illegal characters, and spaces are replaced with {@code _};
     * consecutive underscores are collapsed into one.
     * The original name is preserved as the display name shown in the HTML report.
     */
    private static String sanitizeTestName(String name) {
        return name.replaceAll("[/\\\\:*?\"<>| ]", "_").replaceAll("_+", "_");
    }
}
