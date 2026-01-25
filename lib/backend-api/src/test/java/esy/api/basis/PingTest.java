package esy.api.basis;

import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class PingTest {

    Ping createWithTime(final LocalDateTime time) {
        return Ping.fromJson("""
                {
                    "at":"%s"
                }
                """.formatted(Ping.TIME_FORMATTER.format(time)));
    }

    static Stream<LocalDateTime> atSource() {
        return Stream.of(
                LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 3, 5, 123456789)),
                LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.now()),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2000, 12, 31, 23, 59, 59, 999999999));
    }

    @Test
    void equalsHashcodeToString() {
        final var value = createWithTime(LocalDateTime.now());
        // Identisches Objekt
        assertEquals(value, value);
        assertTrue(value.isEqual(value));
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiche UUID
        final var clone = Ping.fromJson(value.writeJson());
        assertTrue(clone.isEqual(value));
        assertEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Gleicher Zeitpunkt
        final var equal = createWithTime(value.getAt());
        assertNotEquals(equal, value);
        assertTrue(value.isEqual(equal));
        assertNotEquals(equal.hashCode(), value.hashCode());
        assertNotEquals(equal.toString(), value.toString());
        // Anderer Zeitpunkt
        final var other = createWithTime(value.getAt().plusSeconds(1L));
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
        final var value0 = createWithTime(LocalDateTime.now());
        final var value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final var value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"version\": \"1\"",
            "\"created\": \"2019-04-22\"",
            "\"garbage\": \"value\""
    })
    void jsonGarbage(final String line) {
        final var json = "{" + line + "}";
        final var value = Ping.fromJson(json);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
    }

    @ParameterizedTest
    @MethodSource("atSource")
    void json(final LocalDateTime at) {
        final var value = createWithTime(at);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
        assertEquals(at, value.getAt());

        final var json = new JsonJpaMapper().parseJsonNode(value.writeJson());
        assertEquals(0, json.at("/version").asLong());
        assertEquals(Ping.TIME_FORMATTER.format(at), json.at("/at").asText());
    }

    @ParameterizedTest
    @MethodSource("atSource")
    void jsonAt(final LocalDateTime at) {
        final var value = createWithTime(at);
        assertEquals(at, value.getAt());
        value.setAt(at.plusSeconds(1L));
        assertNotEquals(at, value.getAt());
    }
}
