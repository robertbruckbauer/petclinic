package esy.api.clinic;

import esy.api.client.Owner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class VetTest {

	Vet createWithName(final String name) {
		return Vet.parseJson("{" +
				"\"name\":\"" + name + "\"" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Tom";
		final Vet value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Vet clone = createWithName(name);
		assertNotSame(value, clone);
		assertNotEquals(clone, value);
		assertTrue(value.isEqual(clone));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Vet other = createWithName("X" + name);
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
		final String name = "Max Mustermann";
		final Vet value0 = createWithName(name);
		final Vet value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Vet value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String name = "Max Mustermann";
		final Vet value = createWithName(name);
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
		final Owner value = Owner.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}


	@Test
	public void jsonSkill() {
		final String name = "Max Mustermann";
		final Vet value = createWithName(name);
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

		value.getAllSkill().clear();
		value.addAllSkill("A, Z, B", ", ");
		assertEquals(3, value.getAllSkill().size());
		assertEquals(Set.of("A", "B", "Z"), value.getAllSkill());

		value.getAllSkill().clear();
		value.addAllSkill("A,Z;B", ",|;");
		assertEquals(3, value.getAllSkill().size());
		assertEquals(Set.of("A", "B", "Z"), value.getAllSkill());
	}
}
