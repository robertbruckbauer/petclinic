import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;

public class VersionReleaseTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = Git.open(project.getRootDir())) {
            final var fromTag = "HEAD";
            final var toTag = JGit.describeTags(git);
            final var allLog = JGit.listAllLog(git, fromTag, toTag);
            allLog.forEach(log -> System.out.println("* " + log));
            System.out.println();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
