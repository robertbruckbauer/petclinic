import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MessageTesterTest {

    @Test
    void constraints() {
        final var messageTester = new MessageTester();
        assertThrows(NullPointerException.class, () -> messageTester.test(null));
        assertThrows(NullPointerException.class, () -> messageTester.apply(null));
        assertThrows(NullPointerException.class, () -> new Message(null, "", false));
        assertThrows(NullPointerException.class, () -> new Message("", null, false));
    }

    static Stream<String> messageOk() {
        return Stream.of(
                "fix",
                "feat",
                "spike",
                "build",
                "doc",
                "refactor",
                "renovate",
                "EGK-21",
                "egk-21");
    }

    @ParameterizedTest
    @MethodSource("messageOk")
    void messageOk(final String type) {
        final var text = "%s: Lorem ipsum".formatted(type);
        final var messageTester = new MessageTester();
        assertTrue(messageTester.test(text));
        final var message = messageTester.apply(text);
        assertEquals(type, message.type());
        assertEquals(text, message.text());
        assertFalse(message.major());
    }

    @ParameterizedTest
    @MethodSource("messageOk")
    void messageOkBreaking(final String type) {
        final var text = "%s!: Lorem ipsum".formatted(type);
        final var messageTester = new MessageTester();
        assertTrue(messageTester.test(text));
        final var message = messageTester.apply(text);
        assertEquals(type, message.type());
        assertEquals(text, message.text());
        assertTrue(message.major());
    }

    @ParameterizedTest
    @MethodSource("messageOk")
    void messageOkScope(final String type) {
        final var text = "%s(scope): Lorem ipsum".formatted(type);
        final var messageTester = new MessageTester();
        assertTrue(messageTester.test(text));
        final var message = messageTester.apply(text);
        assertEquals(type, message.type());
        assertEquals(text, message.text());
        assertFalse(message.major());
    }

    @ParameterizedTest
    @MethodSource("messageOk")
    void messageOkScopeBreaking(final String type) {
        final var text = "%s(scope)!: Lorem ipsum".formatted(type);
        final var messageTester = new MessageTester();
        assertTrue(messageTester.test(text));
        final var message = messageTester.apply(text);
        assertEquals(type, message.type());
        assertEquals(text, message.text());
        assertTrue(message.major());
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
    void messageNotOk(final String text) {
        final var messageTester = new MessageTester();
        assertFalse(messageTester.test(text));
        final var message = messageTester.apply(text);
        assertEquals("", message.type());
        assertEquals(text, message.text());
    }
}