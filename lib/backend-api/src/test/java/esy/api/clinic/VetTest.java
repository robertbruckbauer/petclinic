package esy.api.clinic;

import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VetTest {

	Vet createWithName(final String name) {
		return Vet.fromJson("""
                {
                	"name":"%s",
                	"allSkill":["Z","A"],
                	"allSpecies":["Dog","Cat"]
                }
                """.formatted(name));
	}

	@Test
	void equalsHashcodeToString() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final var clone = createWithName(name);
		assertNotSame(value, clone);
		assertNotEquals(clone, value);
		assertTrue(value.isEqual(clone));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final var other = createWithName("X" + name);
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
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		final var json = new JsonJpaMapper().parseJsonNode(value.writeJson());
		assertEquals(0, json.at("/version").asLong());
		assertFalse(json.at("/id").isMissingNode());
		assertFalse(json.at("/name").isMissingNode());
		assertFalse(json.at("/allSkill").isMissingNode());
		assertFalse(json.at("/allSpecies").isMissingNode());
	}

	@Test
	void withId() {
		final var name = "Max Mustermann";
		final var value0 = createWithName(name);
		assertNotNull(value0.getId());
		assertNotNull(value0.getName());
		assertNotNull(value0.getAllSkill());
		assertNotNull(value0.getAllSpecies());
		final var value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final var value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void jsonName() {
		final var name = "Mia Musterfrau";
		final var value = Vet.fromJson("""
                        {
                            "name":"%s"
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
                            "name":"%s"
                        }
                        """.formatted(text);
		assertThrows(IllegalArgumentException.class, () -> Vet.fromJson(json).verify());
	}

	@Test
	public void jsonSkill() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertEquals(2, value.getAllSkill().size());
		assertTrue(value.getAllSkill().contains("A"));
		assertTrue(value.getAllSkill().contains("Z"));

		value.addAllSkill("B");
		assertEquals(3, value.getAllSkill().size());
		assertTrue(value.getAllSkill().contains("B"));
	}

	@Test
	public void jsonSpecies() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertEquals(2, value.getAllSpecies().size());
		assertTrue(value.getAllSpecies().contains("Cat"));
		assertTrue(value.getAllSpecies().contains("Dog"));

		value.addAllSpecies("Bird");
		assertEquals(3, value.getAllSpecies().size());
		assertTrue(value.getAllSpecies().contains("Bird"));
	}
}
