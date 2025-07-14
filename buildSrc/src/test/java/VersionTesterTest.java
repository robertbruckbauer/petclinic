import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VersionTesterTest {

    @Test
    void constraints() {
        final var versionTester = new VersionTester();
        assertThrows(NullPointerException.class, () -> versionTester.test(null));
        assertThrows(NullPointerException.class, () -> versionTester.apply(null));
        assertThrows(NullPointerException.class, () -> new Version(null, 0, 0, ""));
        assertThrows(NullPointerException.class, () -> new Version("", 0, 0, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1.2",
            "1.2.0",
            "1.2.0-alpha",
            "v1.2",
    })
    void versionOk(final String tagName) {
        final var versionTester = new VersionTester();
        assertTrue(versionTester.test(tagName));
        final var version = versionTester.apply(tagName);
        assertNotNull(version.prefix());
        assertEquals(1, version.major());
        assertEquals(2, version.minor());
        assertNotNull(version.patch());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            ".",
            ".2",
            "1",
            "1.",
            "1.a",
            "x1.2",
            "v",
            "v1",
            "v1."
    })
    void versionNotOk(final String tagName) {
        final var versionTester = new VersionTester();
        assertFalse(versionTester.test(tagName));
        final var version = versionTester.apply(tagName);
        assertEquals("", version.prefix());
        assertEquals(0, version.major());
        assertEquals(0, version.minor());
        assertEquals("", version.patch());
    }
}