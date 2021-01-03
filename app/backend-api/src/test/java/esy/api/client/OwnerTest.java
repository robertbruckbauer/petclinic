package esy.api.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerTest {

	Owner createWithName(final String name) {
		return Owner.parseJson("{" +
				"\"name\":\"" + name + "\"," +
				"\"address\":\"Bergweg 1, 5400 Hallein\"," +
				"\"contact\":\"+43 660 5557683\"" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Tom";
		final Owner value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Owner clone = createWithName(name);
		assertNotSame(value, clone);
		assertNotEquals(clone, value);
		assertTrue(value.isEqual(clone));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Owner other = createWithName("X" + name);
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
		final Owner value0 = createWithName(name);
		final Owner value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Owner value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String name = "Max Mustermann";
		final Owner value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertFalse(value.getAddress().isBlank());
		assertFalse(value.getContact().isBlank());
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
}
