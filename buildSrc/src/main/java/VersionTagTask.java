import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionTagTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = JGit.open(project.getRootDir())) {
            git.versionTag().ifPresent(System.out::println);
        }
    }
}
