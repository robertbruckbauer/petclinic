package esy.api.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PetTest {

	Pet createWithName(final String name) {
		return Pet.parseJson("{" +
				"\"name\":\"" + name + "\"," +
				"\"born\":\"2021-04-22\"," +
				"\"species\":\"Cat\"" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Tom";
		final Pet value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Pet clone = createWithName(name);
		assertNotSame(value, clone);
		assertNotEquals(clone, value);
		assertTrue(value.isEqual(clone));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Pet other = createWithName("X" + name);
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
		final String name = "Tom";
		final Pet value0 = createWithName(name);
		final Pet value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Pet value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String name = "Tom";
		final Pet value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertEquals(2021, value.getBorn().getYear());
		assertEquals(Month.APRIL, value.getBorn().getMonth());
		assertEquals(22, value.getBorn().getDayOfMonth());
		assertEquals("Cat", value.getSpecies());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\":\"\", \"species\":\"cat\"}",
			"{\"name\":\" \", \"species\":\"cat\"}",
			"{\"name\":\"\\t\", \"species\":\"cat\"}",
			"{\"name\":\"\\n\", \"species\":\"cat\"}",
			"{\"species\":\"\", \"name\":\"Tom\"}",
			"{\"species\":\" \", \"name\":\"Tom\"}",
			"{\"species\":\"\\t\", \"name\":\"Tom\"}",
			"{\"species\":\"\\n\", \"name\":\"Tom\"}"
	})
	void jsonConstraints(final String json) {
		final Pet value = Pet.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}
}
