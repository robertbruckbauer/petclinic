import org.gradle.api.tasks.TaskAction;

public abstract class VersionTagTask extends JGitTaskBase {

    @TaskAction
    public void task() {
        doWithGit(git -> {
            final var cleanTag = git.releaseTag();
            final var localTag = git.versionTag();
            if (!cleanTag.equals(localTag)) {
                if (!localTag.followsAfter(cleanTag)) {
                    throw new IllegalStateException("local tag '%s' does not follow clean tag '%s'".formatted(
                            localTag.toSemVer(), cleanTag.toSemVer()
                    ));
                }
            }
            if (cleanTag.equals(localTag)) {
                getLogger().lifecycle("{}",
                        cleanTag.toSemVer());
            } else {
                getLogger().lifecycle("{} -> {}",
                        cleanTag.toSemVer(),
                        localTag.toSemVer());
            }
        });
    }
}
