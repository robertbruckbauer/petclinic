import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionReleaseTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = JGit.open(project.getRootDir())) {
            final var toTag = git.releaseTag();
            final var allLog = git.listAllLog("HEAD", toTag.toRef());
            allLog.forEach(log -> System.out.printf("* %s%n", log));
            System.out.println();
        }
    }
}
