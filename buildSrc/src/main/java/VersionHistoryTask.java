import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public abstract class VersionHistoryTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVersion();

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @Inject
    public VersionHistoryTask() {
        final var version = getProject().getLayout().getProjectDirectory().file("VERSION");
        getVersion().convention(version);
    }

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
            try (final var os = new PrintWriter(getChangelog().get().getAsFile())) {
                final var allTag = git.listAllTag();
                for (int i = 1; i < allTag.size(); i++) {
                    final var fromTag = allTag.get(i - 1);
                    final var toTag = allTag.get(i);
                    final var allLog = git.listAllLog(fromTag.toRef(), toTag.toRef());
                    os.printf("# Version %s%n", fromTag.toSemVer());
                    os.println();
                    allLog.forEach(log -> os.printf("* %s%n", log));
                    os.println();
                }
            }
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
}
