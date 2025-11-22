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
 * @see <a href="https://semver.org/"/>
 */
@Embeddable
@Data
public class Version {

    // tag::properties[]
    /**
     * The major version X (X.y.z | X > 0) MUST always be increased
     * when incompatible changes are introduced into the public API.
     * Changes MAY also include changes that would otherwise have 
     * increased the minor version or the patch version. When this 
     * version number is increased, the minor version MUST be reset
     * to zero.
     */
    @Column(name = "major")
    @JsonProperty
    private final int major;

    /**
     * The minor version Y (x.Y.z | x > 0) MUST always be increased
     * when new functionality that is compatible with the existing
     * API is released.
     * It MUST also be incremented when a function of the public API
     * is marked as deprecated. If extensive changes to internal code 
     * are introduced, the minor version MAY also be incremented.
     */
    @Column(name = "minor")
    @JsonProperty
    private final int minor;
    // end::properties[]

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
