package esy.api.clinic;

import esy.api.client.Pet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class VisitTest {

    Visit createWithText(final String text) {
        return Visit.parseJson("""
                        {
                            "date":"2021-04-22",
                            "text":"%s"
                        }
                        """.formatted(text))
                .setPet(Pet.parseJson("""
                        {
                            "id":"deadbeef-dead-beef-dead-deadbeefdead",
                            "name":"Tom",
                            "born":"2020-12-24",
                            "species":"Cat"
                        }"""))
                .setVet(Vet.parseJson("""
                        {
                            "id":"deadbeef-dead-beef-dead-deadbeefdead",
                            "name":"John Cleese"
                        }"""));
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
    void withId() {
        final var text = "Lorem Ipsum.";
        final var value0 = createWithText(text);
        final var value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final var value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @Test
    void json() {
        final var text = "Lorem Ipsum.";
        final var value = createWithText(text);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
        assertEquals(2021, value.getDate().getYear());
        assertEquals(Month.APRIL, value.getDate().getMonth());
        assertEquals(22, value.getDate().getDayOfMonth());
        assertNull(value.getTime());
        assertFalse(value.getText().isBlank());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00:00",
            "23:59",
            "24:00"
    })
    void jsonTime(final String time) {
        final var value = Visit.parseJson("""
                        {
                            "date":"2021-04-22",
                            "time":"%s",
                            "text":"Lorem tempus."
                        }
                        """.formatted(time));
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getTime());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00",
            "24:01",
            "23:59:01"
    })
    void jsonTimeConstraint(final String time) {
        assertThrows(IllegalArgumentException.class, () ->
                Visit.parseJson("""
                        {
                            "date":"2021-04-22",
                            "time":"%s",
                            "text":"Lorem tempus."
                        }
                        """.formatted(time)));
    }
}
