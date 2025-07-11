import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

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
            try (final var os = new PrintWriter(getChangelog().get().getAsFile())) {
                final var toTag = git.releaseTag();
                final var allLog = git.listAllLog("HEAD", toTag.toRef());
                allLog.forEach(log -> os.printf("* %s%n", log));
                os.println();
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
