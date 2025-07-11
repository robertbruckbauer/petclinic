import lombok.NonNull;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public abstract class VersionCheckTask extends DefaultTask {

    static final Pattern JSON_VERSION_PATTERN = Pattern.compile(
            "\"version\"\\s*:\\s*\"(\\d+\\.\\d+\\.\\d+)\"");

    static final Pattern YAML_VERSION_PATTERN = Pattern.compile(
            "version\\s*:\\s*(\\d+\\.\\d+\\.\\d+)");

    @InputFile
    public abstract RegularFileProperty getVersion();

    @InputFiles
    public abstract ConfigurableFileCollection getAllFileWithVersion();

    @Inject
    public VersionCheckTask() {
        final var version = getProject().getLayout().getSettingsDirectory().file("VERSION");
        getVersion().convention(version);
    }

    @TaskAction
    public void task() {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
            final var localTag = git.versionTag();
            for (final var file : getAllFileWithVersion().getFiles()) {
                if (file.getName().endsWith(".json")) {
                    checkVersion(JSON_VERSION_PATTERN, localTag, file);
                }
                if (file.getName().endsWith(".yaml")) {
                    checkVersion(YAML_VERSION_PATTERN, localTag, file);
                }
            }
        }
    }

    private @NotNull Path toFilename(@NotNull final File file) {
        return getProject().getRootDir().toPath().relativize(file.toPath());
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
