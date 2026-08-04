package esy.api.clinic;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class VisitTest {

    static Visit createWithText(final String text) {
        return Visit.fromJson("""
                        {
                            "date":"2021-04-22",
                            "time":"10:37",
                            "text":"%s"
                        }
                        """.formatted(text))
                .setPet(createPet("Tom"))
                .setVet(createVet("Dr. Smith"));
    }

    static Pet createPet(final String name) {
        return Pet.fromJson("""
            {
                "id":"deadbeef-dead-beef-dead-deadbeefdead",
                "name":"%s",
                "born":"2021-04-22",
                "species":"Cat"
            }
            """.formatted(name));
    }

    static Vet createVet(final String name) {
        return Vet.fromJson("""
            {
                "id":"deadbeef-dead-beef-dead-deadbeefdead",
                "name":"%s"
            }
            """.formatted(name));
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
        assertFalse(json.at("/text").isMissingNode());
        assertFalse(json.at("/date").isMissingNode());
        assertFalse(json.at("/time").isMissingNode());
        assertFalse(json.at("/billable").isMissingNode());
        assertFalse(json.at("/duration").isMissingNode());
        assertTrue(json.at("/pet").isMissingNode());
        assertTrue(json.at("/vet").isMissingNode());
    }

    @Test
    void withId() {
        final var text = "Lorem Ipsum.";
        final var value0 = createWithText(text);
        assertNotNull(value0.getId());
        assertNotNull(value0.getDate());
        assertNotNull(value0.getTime());
        assertFalse(value0.isBillable());
        assertNotNull(value0.getDuration());
        assertNotNull(value0.getPet());
        assertNotNull(value0.getVet());
        final var value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final var value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @Test
    void jsonText() {
        final var text = "Lorem Ipsum.";
        final var value = Visit.fromJson("""
                        {
                            "date":"2021-04-22",
                            "text":"%s"
                        }
                        """.formatted(text));
        assertDoesNotThrow(value::verify);
        assertEquals(text, value.getText());
    }

    @ParameterizedTest
    @ValueSource(booleans =  {true, false})
    void jsonBillable(final boolean billable) {
        final var value = Visit.fromJson("""
                        {
                            "date":"2021-04-22",
                            "text":"Lorem Ipsum.",
                            "billable":"%b"
                        }
                        """.formatted(billable));
        assertDoesNotThrow(value::verify);
        assertEquals(billable, value.isBillable());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "JA",
            "NEIN",
    })
    void jsonBillableConstraints(final String text) {
        final var json = """
                        {
                            "date":"2021-04-22",
                            "text":"Lorem Ipsum.",
                            "billable":"%s"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Visit.fromJson(json).verify());
    }

    static Stream<LocalDate> jsonDate() {
        return Stream.of(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 4, 22),
                LocalDate.of(2022, 12, 31)
        );
    }

    @ParameterizedTest
    @MethodSource
    void jsonDate(final LocalDate date) {
        final var value = Visit.fromJson("""
                        {
                            "date":"%s",
                            "text":"Lorem Ipsum."
                        }
                        """.formatted(Visit.DATE_FORMATTER.format(date)));
        assertDoesNotThrow(value::verify);
        assertEquals(date, value.getDate());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2022",
            "2022-04",
            "2022-04-32"
    })
    void jsonDateConstraints(final String text) {
        final var json = """
                        {
                            "date":"%s",
                            "text":"Lorem Ipsum."
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Visit.fromJson(json).verify());
    }

    static Stream<LocalTime> jsonTime() {
        return Stream.of(
                LocalTime.of(0, 0, 0),
                LocalTime.of(13, 45, 37),
                LocalTime.of(23, 59, 59)
        );
    }

    @ParameterizedTest
    @MethodSource
    void jsonTime(final LocalTime time) {
        final var value = Visit.fromJson("""
                        {
                            "date":"2021-04-22",
                            "time":"%s",
                            "text":"Lorem Ipsum."
                        }
                        """.formatted(Visit.TIME_FORMATTER.format(time)));
        assertDoesNotThrow(value::verify);
        assertEquals(time.getHour(), value.getTime().getHour());
        assertEquals(time.getMinute(), value.getTime().getMinute());
        assertEquals(0, value.getTime().getSecond());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00",
            "24:01",
            "23:59:01"
    })
    void jsonTimeConstraints(final String text) {
        final var json = """
                        {
                            "date":"2021-04-22",
                            "time":"%s",
                            "text":"Lorem Ipsum."
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Visit.fromJson(json).verify());
    }

    static Stream<Duration> jsonDuration() {
        return Stream.of(
                Duration.ZERO,
                Duration.ofMinutes(30),
                Duration.ofHours(1),
                Duration.ofHours(1).plusMinutes(30)
        );
    }

    @ParameterizedTest
    @ValueSource(longs = {
            0,
            10,
            -1
    })
    void jsonDuration(final long minutes) {
        final var value = Visit.fromJson("""
                        {
                            "date":"2021-04-22",
                            "text":"Lorem Ipsum.",
                            "duration":"%s"
                        }
                        """.formatted(Duration.ofMinutes(minutes)));
        assertDoesNotThrow(value::verify);
        assertEquals(minutes, value.getDuration().toMinutes());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "30",
            "1:30"
    })
    void jsonDurationConstraints(final String text) {
        final var json = """
                        {
                            "date":"2021-04-22",
                            "text":"Lorem Ipsum.",
                            "duration":"%s"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Visit.fromJson(json).verify());
    }
}
