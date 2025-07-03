package esy.api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("fast")
class VersionTest {

    @Test
    void constraints() {
        assertThrows(NullPointerException.class,
                () -> Version.fromString(null));
        assertThrows(IllegalArgumentException.class,
                () -> new Version(-1));
        assertThrows(IllegalArgumentException.class,
                () -> new Version(1, -1));
    }

    @Test
    void withDefaults() {
        final var classUnderTest = new Version();
        final var v0 = new Version();
        assertEquals(v0, classUnderTest);
        assertEquals(v0.hashCode(), classUnderTest.hashCode());
        assertEquals(v0.toString(), classUnderTest.toString());
        final var v1 = new Version(1);
        assertNotEquals(v1, classUnderTest);
        assertNotEquals(v1.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v1.toString(), classUnderTest.toString());
        final var v2 = new Version(2,1);
        assertNotEquals(v2, classUnderTest);
        assertNotEquals(v2.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v2.toString(), classUnderTest.toString());
    }

    @Test
    void withMajor() {
        final var classUnderTest = new Version(1);
        final var v0 = new Version();
        assertNotEquals(v0, classUnderTest);
        assertNotEquals(v0.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v0.toString(), classUnderTest.toString());
        final var v1 = new Version(1);
        assertEquals(v1, classUnderTest);
        assertEquals(v1.hashCode(), classUnderTest.hashCode());
        assertEquals(v1.toString(), classUnderTest.toString());
        final var v2 = new Version(2,1);
        assertNotEquals(v2, classUnderTest);
        assertNotEquals(v2.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v2.toString(), classUnderTest.toString());
    }

    @Test
    void withMinor() {
        final var classUnderTest = new Version(2, 1);
        final var v0 = new Version();
        assertNotEquals(v0, classUnderTest);
        assertNotEquals(v0.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v0.toString(), classUnderTest.toString());
        final var v1 = new Version(1);
        assertNotEquals(v1, classUnderTest);
        assertNotEquals(v1.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v1.toString(), classUnderTest.toString());
        final Version v2 = new Version(2,1);
        assertEquals(v2, classUnderTest);
        assertEquals(v2.hashCode(), classUnderTest.hashCode());
        assertEquals(v2.toString(), classUnderTest.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2.1", "2.1\n", "2.1.0", "2.1.0\n"})
    void fromString(final String value) {
        final var classUnderTest = Version.fromString(new Scanner(value));
        assertEquals(2, classUnderTest.getMajor());
        assertEquals(1, classUnderTest.getMinor());
        assertNotEquals(new Version(2), classUnderTest);
        assertEquals(new Version(2,1), classUnderTest);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1"})
    void fromStringNoSuchElementException(final String value) {
        assertThrows(NoSuchElementException.class,
                () -> Version.fromString(new Scanner(value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,0", "1.0,0", "1.0a", "A", "1A"})
    void fromStringNumberFormatException(final String value) {
        assertThrows(NumberFormatException.class,
                () -> Version.fromString(new Scanner(value)));
    }

    @Test
    void fromResource() {
        final var resource = new ClassPathResource("VERSION");
        final var version = Version.fromResource(resource);
        assertNotNull(version);
    }

    @Test
    void fromResourceMissingResourceException() throws Exception {
        final var resource = mock(Resource.class);
        when(resource.getInputStream())
                .thenThrow(new FileNotFoundException());
        assertThrows(MissingResourceException.class, () ->
                Version.fromResource(resource));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1"})
    void fromResourceNoSuchElementException(final String value) throws Exception {
        final var resource = mock(Resource.class);
        when(resource.getInputStream())
                .thenReturn(new ByteArrayInputStream(value.getBytes(UTF_8)));
        assertThrows(NoSuchElementException.class, () ->
                Version.fromResource(resource));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,0", "1.0,0", "1.0a", "A", "1A"})
    void fromResourceNumberFormatException(final String value) throws Exception {
        final var resource = mock(Resource.class);
        when(resource.getInputStream())
                .thenReturn(new ByteArrayInputStream(value.getBytes(UTF_8)));
        assertThrows(NumberFormatException.class, () ->
                Version.fromResource(resource));
    }
}
