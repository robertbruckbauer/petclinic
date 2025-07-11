import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public abstract class VersionHistoryTask extends DefaultTask {

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir())) {
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
