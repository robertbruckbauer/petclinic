package esy.api.info;

import lombok.Data;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Data
public class Version {

    private final int major;
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
    public static Version fromString(final Scanner scanner) {
        scanner.useDelimiter("\\.");
        if (!scanner.hasNext()) {
            throw new NoSuchElementException("major not set");
        }
        final int major = scanner.nextInt();
        if (!scanner.hasNext()) {
            throw new NoSuchElementException("minor not set");
        }
        final int minor = scanner.nextInt();
        return new Version(major, minor);
    }
}
