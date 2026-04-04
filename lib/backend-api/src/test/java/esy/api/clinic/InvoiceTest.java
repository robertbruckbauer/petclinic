package esy.api.clinic;

import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class InvoiceTest {

    Invoice createWithText(final String text) {
        return Invoice.fromJson("""
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(text));
    }

    @Test
    void equalsHashcodeToString() {
        final var text = "Lorem Ipsum.";
        final var value = createWithText(text);
        // Identisches Objekt
        assertEquals(value, value);
        assertTrue(value.isEqual(value));
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final var clone = createWithText(text);
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertTrue(value.isEqual(clone));
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertNotEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final var other = createWithText("Ex " + text);
        assertNotSame(value, other);
        assertNotEquals(other, value);
        assertFalse(value.isEqual(other));
        assertNotEquals(other.hashCode(), value.hashCode());
        assertNotEquals(other.toString(), value.toString());
        // Kein Objekt
        assertNotEquals(value, null);
        assertFalse(value.isEqual(null));
        // Falsches Objekt
        assertNotEquals(this, value);
    }

    @Test
    void writeJson() {
        final var text = "Lorem Ipsum.";
        final var value = createWithText(text);
        final var json = new JsonJpaMapper().parseJsonNode(value.writeJson());
        assertFalse(json.at("/version").isMissingNode());
        assertFalse(json.at("/id").isMissingNode());
        assertFalse(json.at("/at").isMissingNode());
        assertFalse(json.at("/due").isMissingNode());
        assertFalse(json.at("/status").isMissingNode());
        assertFalse(json.at("/text").isMissingNode());
    }

    @Test
    void withId() {
        final var text = "Lorem Ipsum.";
        final var value0 = createWithText(text);
        assertNotNull(value0.getId());
        assertNotNull(value0.getAt());
        assertNotNull(value0.getDue());
        assertNotNull(value0.getStatus());
        assertNotNull(value0.getText());
        final var value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final var value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @Test
    void jsonText() {
        final var text = "Lorem Ipsum.";
        final var value = Invoice.fromJson("""
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(text));
        assertDoesNotThrow(value::verify);
        assertEquals(text, value.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "\\t",
            "\\n"
    })
    void jsonTextConstraints(final String text) {
        final var json = """
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Invoice.fromJson(json).verify());
    }

    @ParameterizedTest
    @EnumSource(InvoiceStatus.class)
    void jsonStatus(final InvoiceStatus status) {
        final var value = Invoice.fromJson("""
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
                    "status":"%s",
                    "text":"Lorem Ipsum."
                }
                """.formatted(status.name()));
        assertDoesNotThrow(value::verify);
        assertEquals(status, value.getStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "DRAFTED"
    })
    void jsonStatusConstraints(final String text) {
        final var json = """
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
                    "status":"%s",
                    "text":"Lorem Ipsum."
                }
                """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Invoice.fromJson(json).verify());
    }

    static Stream<LocalDate> jsonAt() {
        return Stream.of(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 4, 15),
                LocalDate.of(2024, 12, 31)
        );
    }

    @ParameterizedTest
    @MethodSource
    void jsonAt(final LocalDate date) {
        final var value = Invoice.fromJson("""
                {
                    "at":"%s",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"Lorem Ipsum."
                }
                """.formatted(Invoice.DATE_FORMATTER.format(date)));
        assertDoesNotThrow(value::verify);
        assertEquals(date, value.getAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2024",
            "2024-04",
            "2024-04-32"
    })
    void jsonAtConstraints(final String text) {
        final var json = """
                {
                    "at":"%s",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"Lorem Ipsum."
                }
                """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Invoice.fromJson(json).verify());
    }

    static Stream<LocalDate> jsonDue() {
        return Stream.of(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 4, 15),
                LocalDate.of(2024, 12, 31)
        );
    }

    @ParameterizedTest
    @MethodSource
    void jsonDue(final LocalDate date) {
        final var value = Invoice.fromJson("""
                {
                    "at":"2024-01-15",
                    "due":"%s",
                    "status":"D",
                    "text":"Lorem Ipsum."
                }
                """.formatted(Invoice.DATE_FORMATTER.format(date)));
        assertDoesNotThrow(value::verify);
        assertEquals(date, value.getDue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2024",
            "2024-04",
            "2024-04-32"
    })
    void jsonDueConstraints(final String text) {
        final var json = """
                {
                    "at":"2024-01-15",
                    "due":"%s",
                    "status":"D",
                    "text":"Lorem Ipsum."
                }
                """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Invoice.fromJson(json).verify());
    }
}
