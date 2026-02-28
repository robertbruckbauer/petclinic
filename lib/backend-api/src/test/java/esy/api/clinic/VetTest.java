package esy.api.clinic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VetTest {

	Vet createWithName(final String name) {
		return Vet.fromJson("""
                {
                	"name":"%s"
				}
				""".formatted(name));
	}

	@Test
	void equalsHashcodeToString() {
		final var name = "Tom";
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
	void withId() {
		final var name = "Max Mustermann";
		final var value0 = createWithName(name);
		final var value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final var value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\": \"\"}",
			"{\"name\": \" \"}",
			"{\"name\": \"\\t\"}"
	})
	void jsonConstraints(final String json) {
		assertThrows(IllegalArgumentException.class, () -> Vet.fromJson(json).verify());
	}


	@Test
	public void jsonSkill() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());

		value.getAllSkill().add("A");
		assertDoesNotThrow(value::verify);
		assertEquals(1, value.getAllSkill().size());
		assertTrue(value.getAllSkill().contains("A"));

		value.getAllSkill().clear();
		assertDoesNotThrow(value::verify);
		assertEquals(0, value.getAllSkill().size());

		value.getAllSkill().addAll(Set.of("A", "B"));
		assertDoesNotThrow(value::verify);
		assertEquals(2, value.getAllSkill().size());
		assertTrue(value.getAllSkill().contains("A"));
		assertTrue(value.getAllSkill().contains("B"));

		value.getAllSkill().remove("B");
		assertDoesNotThrow(value::verify);
		assertEquals(1, value.getAllSkill().size());
		assertTrue(value.getAllSkill().contains("A"));

		value.getAllSkill().clear();
		value.addAllSkill("A", "Z", "B");
		assertEquals(3, value.getAllSkill().size());
		assertEquals(Set.of("A", "B", "Z"), value.getAllSkill());
	}
}
