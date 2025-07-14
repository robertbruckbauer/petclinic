import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public abstract class VersionHistoryTask extends JGitTaskBase {

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @TaskAction
    public void task() {
        doWithGit(git -> {
            final var localTag = git.versionTag();
            final var outputFile = getChangelog().get().getAsFile();
            try (final var writer = new PrintWriter(outputFile)) {
                final var allTag = git.listAllTag();
                for (int i = 1; i < allTag.size(); i++) {
                    final var fromTag = allTag.get(i - 1);
                    final var toTag = allTag.get(i);
                    final var allLog = git.listAllLog(fromTag.toRef(), toTag.toRef());
                    if (i > 1) writer.println();
                    writer.printf("# Version %s%n%n", fromTag.toSemVer());
                    allLog.forEach(log -> writer.printf("* %s%n", log));
                }
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
            getLogger().lifecycle("'{}' successfully created for local tag '{}'",
                    toFilename(outputFile),
                    localTag.toSemVer());
        });
    }
}
