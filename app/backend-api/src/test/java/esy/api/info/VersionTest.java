package esy.api.info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    public void constraints() {
        assertThrows(IllegalArgumentException.class,
                () -> new Version(-1));
        assertThrows(IllegalArgumentException.class,
                () -> new Version(1, -1));
    }

    @Test
    public void withDefaults() {
        final Version classUnderTest = new Version();
        assertEquals(classUnderTest, classUnderTest);
        final Version v0 = new Version();
        assertEquals(v0, classUnderTest);
        assertEquals(v0.hashCode(), classUnderTest.hashCode());
        assertEquals(v0.toString(), classUnderTest.toString());
        final Version v1 = new Version(1);
        assertNotEquals(v1, classUnderTest);
        assertNotEquals(v1.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v1.toString(), classUnderTest.toString());
        final Version v2 = new Version(2,1);
        assertNotEquals(v2, classUnderTest);
        assertNotEquals(v2.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v2.toString(), classUnderTest.toString());
    }

    @Test
    public void withMajor() {
        final Version classUnderTest = new Version(1);
        assertEquals(classUnderTest, classUnderTest);
        final Version v0 = new Version();
        assertNotEquals(v0, classUnderTest);
        assertNotEquals(v0.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v0.toString(), classUnderTest.toString());
        final Version v1 = new Version(1);
        assertEquals(v1, classUnderTest);
        assertEquals(v1.hashCode(), classUnderTest.hashCode());
        assertEquals(v1.toString(), classUnderTest.toString());
        final Version v2 = new Version(2,1);
        assertNotEquals(v2, classUnderTest);
        assertNotEquals(v2.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v2.toString(), classUnderTest.toString());
    }

    @Test
    public void withMinor() {
        final Version classUnderTest = new Version(2, 1);
        assertEquals(classUnderTest, classUnderTest);
        final Version v0 = new Version();
        assertNotEquals(v0, classUnderTest);
        assertNotEquals(v0.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v0.toString(), classUnderTest.toString());
        final Version v1 = new Version(1);
        assertNotEquals(v1, classUnderTest);
        assertNotEquals(v1.hashCode(), classUnderTest.hashCode());
        assertNotEquals(v1.toString(), classUnderTest.toString());
        final Version v2 = new Version(2,1);
        assertEquals(v2, classUnderTest);
        assertEquals(v2.hashCode(), classUnderTest.hashCode());
        assertEquals(v2.toString(), classUnderTest.toString());
    }

    @Test
    public void withScanner() {
        assertThrows(NullPointerException.class,
                () -> Version.fromString(null));
        assertThrows(NoSuchElementException.class,
                () -> Version.fromString(new Scanner("")));
        assertThrows(NoSuchElementException.class,
                () -> Version.fromString(new Scanner("1")));
        assertThrows(InputMismatchException.class,
                () -> Version.fromString(new Scanner("A")));
        final Version classUnderTest = Version.fromString(new Scanner("2.1"));
        assertNotEquals(new Version(2), classUnderTest);
        assertNotEquals(new Version(20,1), classUnderTest);
        assertEquals(new Version(2,1), classUnderTest);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "1,0"})
    public void withNoSuchElementException(final String value) {
        assertThrows(NoSuchElementException.class,
                () -> Version.fromString(new Scanner(value)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "1A", "1.0a"})
    public void withInputMismatchException(final String value) {
        assertThrows(InputMismatchException.class,
                () -> Version.fromString(new Scanner(value)));
    }
}
