import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.List;

public abstract class BuildChangelogTask extends JGitTaskBase {

    @OutputFile
    public abstract RegularFileProperty getChangelog();

    @Input
    public abstract ListProperty<String> getAllIgnoreCommitOfType();

    @Inject
    public BuildChangelogTask() {
        super();
        // allow only conventional commits
        getAllIgnoreCommitOfType().convention(List.of(""));
    }

    @TaskAction
    public void task() {
        doWithGit(git -> {
            final var outputFile = getChangelog().get().getAsFile();
            try (final var writer = new PrintWriter(outputFile)) {
                final var allTag = git.listAllTag();
                if (allTag.size() > 2) {
                    final var fromTag = allTag.get(0);
                    final var toTag = allTag.get(1);
                    final var allIgnoreCommitOfType = getAllIgnoreCommitOfType().get();
                    final var allLog = git.listAllLog(fromTag.toRef(), toTag.toRef());
                    allLog.stream()
                            .filter(log -> !allIgnoreCommitOfType.contains(log.type()))
                            .forEach(log -> writer.printf("* %s%n", log.text()));
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
