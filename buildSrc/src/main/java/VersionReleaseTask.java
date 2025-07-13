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
public abstract class VersionReleaseTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVersion();

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @Inject
    public VersionReleaseTask() {
        final var version = getProject().getLayout().getSettingsDirectory().file("VERSION");
        getVersion().convention(version);
    }

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
            final var localTag = git.versionTag();
            final var outputFile = getChangelog().get().getAsFile();
            try (final var writer = new PrintWriter(outputFile)) {
                final var toTag = git.releaseTag();
                final var allLog = git.listAllLog("HEAD", toTag.toRef());
                allLog.forEach(log -> writer.printf("* %s%n", log));
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
            getLogger().lifecycle("'{}' successfully created for local tag '{}'",
                    toFilename(outputFile),
                    localTag.toSemVer());
        }
    }

    private @NotNull Path toFilename(@NotNull final File file) {
        return getProject().getRootDir().toPath().relativize(file.toPath());
    }
}
