import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MessageTesterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "fix",
            "feat",
            "spike",
            "build",
            "doc",
            "refactor",
            "renovate"
    })
    void messageOk(final String typ) {
        final var classUnderTest = new MessageTester();
        assertTrue(classUnderTest.test("%s: Lorem ipsum".formatted(typ)));
        assertTrue(classUnderTest.test("%s(scope): Lorem ipsum".formatted(typ)));
        assertTrue(classUnderTest.test("%s!: Lorem ipsum".formatted(typ)));
        assertTrue(classUnderTest.test("%s(scope)!: Lorem ipsum".formatted(typ)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "fix me",
            "fix me:",
            "fix()",
            "fix(me too)"
    })
    void messageNotOk(final String message) {
        final var classUnderTest = new MessageTester();
        assertFalse(classUnderTest.test(message));
    }
}