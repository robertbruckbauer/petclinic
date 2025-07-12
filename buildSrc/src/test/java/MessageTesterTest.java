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
            "renovate",
            "EGK-21",
            "egk-21"
    })
    void messageOk(final String typ) {
        final var messageTester = new MessageTester();
        assertTrue(messageTester.test("%s: Lorem ipsum".formatted(typ)));
        assertTrue(messageTester.test("%s(scope): Lorem ipsum".formatted(typ)));
        assertTrue(messageTester.test("%s!: Lorem ipsum".formatted(typ)));
        assertTrue(messageTester.test("%s(scope)!: Lorem ipsum".formatted(typ)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "fix me",
            "fix me:",
            "fix()",
            "fix(me too)",
            "EGK",
            "EGK-",
            "EGK-a"
    })
    void messageNotOk(final String message) {
        final var messageTester = new MessageTester();
        assertFalse(messageTester.test(message));
    }
}