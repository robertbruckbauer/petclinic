import lombok.NonNull;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public abstract class VersionCheckTask extends JGitTaskBase {

    static final Pattern JSON_VERSION_PATTERN = Pattern.compile(
            "\"version\"\\s*:\\s*\"(\\d+\\.\\d+\\.\\d+)\"");

    static final Pattern YAML_VERSION_PATTERN = Pattern.compile(
            "version\\s*:\\s*(\\d+\\.\\d+\\.\\d+)");

    @InputFiles
    public abstract ConfigurableFileCollection getAllFileWithVersion();

    @TaskAction
    public void task() {
        doWithGit(git -> {
            final var localTag = git.versionTag();
            for (final var file : getAllFileWithVersion().getFiles()) {
                if (file.getName().endsWith(".json")) {
                    checkVersion(JSON_VERSION_PATTERN, localTag, file);
                }
                if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {
                    checkVersion(YAML_VERSION_PATTERN, localTag, file);
                }
                getLogger().lifecycle("'{}' matches local tag '{}'",
                        toFilename(file),
                        localTag.toSemVer());
            }
        });
    }

    private void checkVersion(@NonNull final Pattern pattern, @NonNull final VersionTag localTag, @NonNull final File file) {
        try {
            final var content = Files.readString(file.toPath());
            final var matcher = pattern.matcher(content);
            if (matcher.find()) {
                final var version = matcher.group(1);
                if (!version.startsWith(localTag.toSemVer())) {
                    throw new IllegalStateException("%s: version '%s' does not match tag '%s'".formatted(
                            toFilename(file), version, localTag.toSemVer()
                    ));
                }
            } else {
                throw new IllegalStateException("%s: version does not exist".formatted(
                        toFilename(file)
                ));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
