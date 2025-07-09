import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionTagTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = JGit.open(project.getRootDir())) {
            final var toTag = git.versionTag();
            System.out.println(toTag.toSemVer());
        }
    }
}
