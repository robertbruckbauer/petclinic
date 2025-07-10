import lombok.NonNull;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class VersionCheckTask extends DefaultTask {

    static final Pattern JSON_VERSION_PATTERN = Pattern.compile(
            "\"version\"\\s*:\\s*\"(\\d+\\.\\d+\\.\\d+)\"");

    static final Pattern YAML_VERSION_PATTERN = Pattern.compile(
            "version\\s*:\\s*(\\d+\\.\\d+\\.\\d+)");

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir())) {
            final var cleanTag = git.releaseTag();
            final var localTag = git.versionTag();
            checkTag(localTag, cleanTag);
            final var projectDir = getProject().getLayout().getProjectDirectory();
            List.of("package.json", "package-lock.json").forEach(filename ->
                    checkVersion(JSON_VERSION_PATTERN, localTag, projectDir.file(filename).getAsFile()));
            List.of("Chart.yaml").forEach(filename ->
                    checkVersion(YAML_VERSION_PATTERN, localTag, projectDir.file(filename).getAsFile()));
        }
    }

    private @NotNull Path toFilename(@NotNull final File file) {
        return getProject().getRootDir().toPath().relativize(file.toPath());
    }

    private void checkTag(@NonNull final VersionTag localTag, @NonNull final VersionTag cleanTag) {
        if (!localTag.followsAfter(cleanTag)) {
            throw new IllegalStateException("local tag '%s' does not follow clean tag '%s'".formatted(
                    localTag.toSemVer(), cleanTag.toSemVer()
            ));
        }
    }

    private void checkVersion(@NonNull final Pattern pattern, @NonNull final VersionTag localTag, @NonNull final File file) {
        if (file.exists()) {
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
}
