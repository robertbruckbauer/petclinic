import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;

public class VersionTagTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = Git.open(project.getRootDir())) {
            System.out.println(JGit.describeTags(git));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GitAPIException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
