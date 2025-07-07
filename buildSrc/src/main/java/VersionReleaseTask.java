import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionReleaseTask extends DefaultTask {

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = JGit.open(project.getRootDir())) {
            git.releaseTag().ifPresent(toTag -> {
                final var allLog = git.listAllLog("HEAD", toTag.toString());
                allLog.forEach(log -> System.out.println("* " + log));
                System.out.println();
            });
        }
    }
}
