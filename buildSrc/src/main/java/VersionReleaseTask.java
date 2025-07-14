import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public abstract class VersionReleaseTask extends JGitTaskBase {

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @TaskAction
    public void task() {
        doWithGit(git -> {
            final var localTag = git.versionTag();
            final var outputFile = getChangelog().get().getAsFile();
            try (final var writer = new PrintWriter(outputFile)) {
                final var toTag = git.releaseTag();
                final var allLog = git.listAllLog("HEAD", toTag.toRef());
                allLog.forEach(log -> writer.printf("* %s%n", log.text()));
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
            getLogger().lifecycle("'{}' successfully created for local tag '{}'",
                    toFilename(outputFile),
                    localTag.toSemVer());
        });
    }
}
