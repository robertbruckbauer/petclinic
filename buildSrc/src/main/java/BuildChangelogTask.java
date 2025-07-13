import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;

@DisableCachingByDefault(because = "use always the latest tags and commits")
public abstract class BuildChangelogTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVersion();

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @Inject
    public BuildChangelogTask() {
        final var version = getProject().getLayout().getSettingsDirectory().file("VERSION");
        getVersion().convention(version);
    }

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
            final var outputFile = getChangelog().get().getAsFile();
            try (final var writer = new PrintWriter(outputFile)) {
                final var allTag = git.listAllTag();
                if (allTag.size() > 2) {
                    final var fromTag = allTag.get(0);
                    final var toTag = allTag.get(1);
                    final var allLog = git.listAllLog(fromTag.toRef(), toTag.toRef());
                    allLog.forEach(log -> writer.printf("* %s%n", log));
                    getLogger().lifecycle("'{}' successfully created from clean tags '{}' to '{}'",
                            toFilename(outputFile),
                            fromTag.toSemVer(),
                            toTag.toSemVer());
                }
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private @NotNull Path toFilename(@NotNull final File file) {
        return getProject().getRootDir().toPath().relativize(file.toPath());
    }
}
