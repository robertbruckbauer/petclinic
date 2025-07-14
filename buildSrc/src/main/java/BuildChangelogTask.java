import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public abstract class BuildChangelogTask extends JGitTaskBase {

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @TaskAction
    public void task() {
        doWithGit(git -> {
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
        });
    }
}
