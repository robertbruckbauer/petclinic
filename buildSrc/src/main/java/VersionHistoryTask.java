import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionHistoryTask extends DefaultTask {

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir())) {
            final var allTag = git.listAllTag();
            for (int i = 1; i < allTag.size(); i++) {
                final var fromTag = allTag.get(i - 1);
                final var toTag = allTag.get(i);
                final var allLog = git.listAllLog(fromTag.toRef(), toTag.toRef());
                System.out.printf("# Version %s%n", fromTag.toSemVer());
                System.out.println();
                allLog.forEach(log -> System.out.printf("* %s%n", log));
                System.out.println();
            }
        }
    }
}
