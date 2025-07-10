import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VersionTesterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "refs/tags/1.2",
            "refs/tags/1.2.0",
            "refs/tags/1.2.0-alpha",
            "refs/tags/v1.2"
    })
    void versionOk(final String tagName) {
        final var classUnderTest = new VersionTester();
        assertTrue(classUnderTest.test(tagName));
        assertEquals("[1, 2]", Arrays.toString(classUnderTest.apply(tagName)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "1.2",
            "refs/1.2",
            "tags/1.2",
            "ref/tags1.2",
            "refs/tag/1.2",
            "refs/tags/1",
            "refs/tags/1.",
            "refs/tags/1.a",
            "refs/tags/x1.2"
    })
    void versionNotOk(final String tagName) {
        final var classUnderTest = new VersionTester();
        assertFalse(classUnderTest.test(tagName));
        assertEquals("[0, 0]", Arrays.toString(classUnderTest.apply(tagName)));
    }
}