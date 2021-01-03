package esy.app.info;

import esy.api.info.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Repository style accessor for {@code VERSION} file from classpath.
 */
@Component
@RequiredArgsConstructor
public class VersionRepository {

    public static final String VERSION_FILE_NAME = "VERSION";

    @Value("classpath:" + VERSION_FILE_NAME)
    private Resource resource;

    /**
     * Returns the content of the {@code VERSION} file from classpath.
     *
     * @return content of the {@code VERSION} file from classpath
     * @throws MissingResourceException if {@code VERSION} file does not exist
     * @throws NoSuchElementException if {@code VERSION} file is not valid
     * @throws InputMismatchException if {@code VERSION} file is corrupt
     */
    public Version find() {
        try (final Scanner scanner = new Scanner(resource.getInputStream())) {
            return Version.fromString(scanner);
        } catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "classpath", VERSION_FILE_NAME);
        }
    }
}
