package esy.api.client;

import esy.api.basis.Sex;
import esy.api.clinic.Visit;
import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {

    Pet createWithName(final String name, final LocalDate born) {
        final var owner = Owner.fromJson("""
                {
                    "id":"deadbeef-dead-beef-dead-deadbeefdead",
                    "name":"Max Mustermann",
                    "address":"Bergweg 1, 5400 Hallein"
                }
                """);
        return Pet.fromJson("""
                        {
                        	"name":"%s",
                        	"born":"%s",
                        	"species":"Cat",
                        	"sex":"M"
                        }
                        """.formatted(name, Pet.DATE_FORMATTER.format(born)))
                .setOwner(owner);
    }

    @Test
    void equalsHashcodeToString() {
        final var name = "Tom";
        final var value = createWithName(name, LocalDate.now());
        // Identisches Objekt
        assertEquals(value, value);
        assertTrue(value.isEqual(value));
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final var clone = createWithName(name, LocalDate.now());
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertTrue(value.isEqual(clone));
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertNotEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final var other = createWithName(name.toUpperCase(), LocalDate.now());
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
        final var name = "Tom";
        final var value = createWithName(name, LocalDate.now());
        final var json = new JsonJpaMapper().parseJsonNode(value.writeJson());
        assertEquals(0, json.at("/version").asLong());
        assertFalse(json.at("/id").isMissingNode());
        assertFalse(json.at("/name").isMissingNode());
        assertFalse(json.at("/born").isMissingNode());
        assertFalse(json.at("/species").isMissingNode());
        assertFalse(json.at("/sex").isMissingNode());
        assertTrue(json.at("/owner").isMissingNode());
    }

    @Test
    void withId() {
        final var name = "Tom";
        final var value0 = createWithName(name, LocalDate.now());
        assertNotNull(value0.getId());
        assertNotNull(value0.getName());
        assertNotNull(value0.getBorn());
        assertNotNull(value0.getSpecies());
        assertNotNull(value0.getSex());
        assertNotNull(value0.getOwner());
        final var value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final var value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @Test
    void jsonName() {
        final var name = "Odi";
        final var value = Pet.fromJson("""
                        {
                            "name":"%s",
                            "born":"2007-03-09",
                            "species":"Cat",
                            "sex":"M"
                        }
                        """.formatted(name));
        assertDoesNotThrow(value::verify);
        assertEquals(name, value.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "\\t",
            "\\n"
    })
    void jsonNameConstraints(final String text) {
        final var json = """
                        {
                            "name":"%s",
                            "born":"2007-03-09",
                            "species":"Cat",
                            "sex":"M"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Pet.fromJson(json).verify());
    }

    static Stream<LocalDate> jsonBorn() {
        return Stream.of(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 4, 22),
                LocalDate.of(2022, 12, 31)
        );
    }

    @ParameterizedTest
    @MethodSource
    void jsonBorn(final LocalDate date) {
        final var value = Visit.fromJson("""
                        {
                            "name":"Tom",
                            "date":"%s",
                            "species":"Cat",
                            "sex":"M"
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
    void jsonBornConstraints(final String text) {
        final var json = """
                        {
                            "name":"Tom",
                            "date":"%s",
                            "species":"Cat",
                            "sex":"M"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Visit.fromJson(json).verify());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Dog", "Bird"})
    void jsonSpecies(final String species) {
        final var value = Pet.fromJson("""
                        {
                            "name":"Jerry",
                            "born":"2007-03-09",
                            "species":"%s",
                            "sex":"M"
                        }
                        """.formatted(species));
        assertDoesNotThrow(value::verify);
        assertEquals(species, value.getSpecies());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "\\t",
            "\\n"
    })
    void jsonSpeciesConstraints(final String text) {
        final var json = """
                        {
                            "name":"Jerry",
                            "born":"2007-03-09",
                            "species":"%s",
                            "sex":"M"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Pet.fromJson(json).verify());
    }

    @ParameterizedTest
    @EnumSource(Sex.class)
    void jsonSex(final Sex sex) {
        final var value = Pet.fromJson("""
                        {
                            "name":"Odi",
                            "born":"2007-03-09",
                            "species":"Dog",
                            "sex":"%s"
                        }
                        """.formatted(sex.name()));
        assertDoesNotThrow(value::verify);
        assertEquals(sex, value.getSex());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "X"
    })
    void jsonSexConstraints(final String text) {
        final var json = """
                        {
                            "name":"Odi",
                            "born":"2007-03-09",
                            "species":"Dog",
                            "sex":"%s"
                        }
                        """.formatted(text);
        assertThrows(IllegalArgumentException.class, () -> Pet.fromJson(json).verify());
    }
}
