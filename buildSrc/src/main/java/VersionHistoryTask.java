import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;

public class VersionHistoryTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = Git.open(project.getRootDir())) {
            final var allTag = JGit.listAllTag(git);
            for (int i = 1; i < allTag.size(); i++) {
                final var fromTag = allTag.get(i - 1).tagName();
                final var toTag = allTag.get(i).tagName();
                final var allLog = JGit.listAllLog(git, fromTag, toTag);
                System.out.println(fromTag.replace("refs/tags/", "# Version "));
                System.out.println();
                allLog.forEach(log -> System.out.println("* " + log));
                System.out.println();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
