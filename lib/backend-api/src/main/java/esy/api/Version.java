package esy.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;
import org.springframework.core.io.Resource;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Version-Objekt mit der Version der Anwendung.
 *
 * @see <a href="https://de.wikipedia.org/wiki/Version_(Software)" />
 * @see <a href="https://semver.org/"/>
 */
@Embeddable
@Data
public class Version {

    /**
     * Die Major-Version X (X.y.z | X > 0) muss (MUST) immer dann erhöht
     * werden, wenn API-inkompatible Änderungen in die öffentlichen API
     * eingeführt werden. Die Änderungen dürfen (MAY) auch Änderungen
     * umfassen, die ansonsten die Minor Version oder die Patch Version
     * erhöht hätten. Wenn diese Versionsnummer erhöht wird, muss (MUST)
     * sowohl die Minor-Version als auch die Patch Version auf Null
     * zurückgesetzt werden.
     */
    @Column(name = "major")
    @JsonProperty
    private final int major;

    /**
     * Die Minor-Version Y (x.Y.z | x > 0) muss (MUST) erhöht werden,
     * wenn neue Funktionalitäten, welche kompatibel zur bisherigen
     * API sind, veröffentlicht werden.
     * Sie muss (MUST) außerdem erhöht werden, wenn eine Funktion der
     * öffentlichen API als deprecated markiert wird. Wenn umfangreiche
     * Änderungen an internem Code eingeführt werden, darf (MAY) die
     * Minor Version ebenfalls erhöht werden. Wenn diese Versionsnummer
     * erhöht wird, muss (MUST) die Patch-Version auf Null zurückgesetzt
     * werden.
     */
    @Column(name = "minor")
    @JsonProperty
    private final int minor;

    public Version(final int major, final int minor) {
        if (major < 0) {
            throw new IllegalArgumentException("major is negative");
        }
        this.major = major;
        if (minor < 0) {
            throw new IllegalArgumentException("minor is negative");
        }
        this.minor = minor;
    }

    public Version() {
        this(0, 0);
    }

    public Version(final int major) {
        this(major, 0);
    }

    @JsonProperty("version")
    @Override
    public String toString() {
        return major + "." + minor;
    }

    /**
     * Creates a version from a version string.
     *
     * @param scanner version string scanner
     * @return version
     * @throws NoSuchElementException if some data does not exist
     * @throws InputMismatchException if some data is not valid
     */
    public static Version fromString(@NonNull final Scanner scanner) {
        scanner.useDelimiter("\\.");
        if (!scanner.hasNext()) {
            throw new NoSuchElementException("major not set");
        }
        final var major = Integer.parseInt(scanner.next().trim());
        if (!scanner.hasNext()) {
            throw new NoSuchElementException("minor not set");
        }
        final var minor = Integer.parseInt(scanner.next().trim());
        return new Version(major, minor);
    }

    /**
     * Creates a version from the contents of a resource.
     *
     * @param resource resource
     * @return version
     * @throws MissingResourceException if resource does not exist
     * @throws NoSuchElementException if resource is not valid
     * @throws InputMismatchException if resource is corrupt
     */
    public static Version fromResource(@NonNull final Resource resource) {
        try (final Scanner scanner = new Scanner(resource.getInputStream())) {
            return Version.fromString(scanner);
        } catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "classpath", resource.getFilename());
        }
    }
}
