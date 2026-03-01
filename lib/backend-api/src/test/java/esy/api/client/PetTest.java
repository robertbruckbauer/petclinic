package esy.api.client;

import esy.api.basis.Sex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetTest {

	Pet createWithName(final String name) {
		return Pet.fromJson("""
                {
                	"name":"%s",
                	"born":"2021-04-22",
                	"species":"Cat",
                	"sex":"M"
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
		final var name = "Tom";
		final var value0 = createWithName(name);
		final var value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final var value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final var name = "Tom";
		final var value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertEquals(2021, value.getBorn().getYear());
		assertEquals(Month.APRIL, value.getBorn().getMonth());
		assertEquals(22, value.getBorn().getDayOfMonth());
		assertEquals("Cat", value.getSpecies());
		assertEquals(Sex.M, value.getSex());
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
		assertThrows(IllegalArgumentException.class, () -> Pet.fromJson(json).verify());
	}

	@ParameterizedTest
	@EnumSource(Sex.class)
	void jsonSex(final Sex sex) {
		final var value = Pet.fromJson("""
                {
                	"name":"%s",
                	"born":"2021-04-22",
                	"species":"Cat",
                	"sex":"%s"
				}
				""".formatted(sex.getText(), sex.name()));
		assertDoesNotThrow(value::verify);
		assertEquals(sex, value.getSex());
	}
}
