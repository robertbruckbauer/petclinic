import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.DisableCachingByDefault;

import javax.inject.Inject;

@DisableCachingByDefault(because = "use always the latest tags and commits")
public abstract class VersionTagTask extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVersion();

    @Inject
    public VersionTagTask() {
        final var version = getProject().getLayout().getSettingsDirectory().file("VERSION");
        getVersion().convention(version);
    }

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
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
        }
    }
}
