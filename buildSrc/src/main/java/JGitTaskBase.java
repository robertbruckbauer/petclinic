import lombok.NonNull;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public abstract class JGitTaskBase extends DefaultTask {

    @InputFile
    public abstract RegularFileProperty getVersion();

    protected JGitTaskBase() {
        final var version = getProject().getLayout().getSettingsDirectory().file("VERSION");
        getVersion().convention(version);
    }

    protected void doWithGit(@NonNull final Consumer<JGit> task) {
        try (final var git = JGit.open(getProject().getRootDir(), getVersion().get().getAsFile())) {
            task.accept(git);
        }
    }

    protected @NotNull Path toFilename(@NotNull final File file) {
        return getProject().getRootDir().toPath().relativize(file.toPath());
    }
}
