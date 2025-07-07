import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class VersionHistoryTask extends DefaultTask {

    private final MessageTester messageTester = new MessageTester();

    @TaskAction
    public void task() {
        final var project = getProject();
        try (final var git = JGit.open(project.getRootDir())) {
            final var allTag = git.listAllTag();
            for (int i = 1; i < allTag.size(); i++) {
                final var fromTag = allTag.get(i - 1).toString();
                final var toTag = allTag.get(i).toString();
                final var allLog = git.listAllLog(fromTag, toTag, messageTester);
                System.out.println(fromTag.replace("refs/tags/", "# Version "));
                System.out.println();
                allLog.forEach(log -> System.out.println("* " + log));
                System.out.println();
            }
        }
    }
}
