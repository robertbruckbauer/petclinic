import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VersionTesterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1.2",
            "1.2.0",
            "1.2.0-alpha",
            "v1.2"
    })
    void versionOk(final String tagName) {
        final var classUnderTest = new VersionTester();
        assertTrue(classUnderTest.test(tagName));
        assertEquals("[1, 2]", Arrays.toString(classUnderTest.apply(tagName)));
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
        final var classUnderTest = new VersionTester();
        assertFalse(classUnderTest.test(tagName));
        assertEquals("[0, 0]", Arrays.toString(classUnderTest.apply(tagName)));
    }
}