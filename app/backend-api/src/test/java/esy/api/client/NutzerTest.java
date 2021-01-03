package esy.api.team;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class NutzerTest {

	Nutzer createWithName(final String name) {
		return Nutzer.parseJson("{" +
				"\"version\": \"1\"," +
				"\"mail\": \"" + name + "@a.de\"," +
				"\"name\":\"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"allSprache\": [\"EN\"]" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Max Mustermann";
		final Nutzer value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Nutzer clone = createWithName(name);
		assertNotSame(clone, value);
		assertNotEquals(clone, value);
		assertTrue(clone.isEqual(value));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Nutzer other = createWithName("X" + name);
		assertNotSame(other, value);
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
		final String name = "Max.Mustermann";
		final Nutzer value0 = createWithName(name);
		final Nutzer value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Nutzer value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String name = "Max Mustermann";
		final Nutzer value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"aktiv\": \"false\"}",
			"{\"mail\": \"\"}",
			"{\"mail\": \"\", \"name\": \"Me\"}",
			"{\"mail\": \" \", \"name\": \"Me\"}",
			"{\"mail\": \"\\t\", \"name\": \"Me\"}",
			"{\"name\": \"\"}",
			"{\"name\": \"\", \"mail\": \"me@a.de\"}",
			"{\"name\": \" \", \"mail\": \"me@a.de\"}",
			"{\"name\": \"\\t\", \"mail\": \"me@a.de\"}"
	})
	void jsonConstraints(final String json) {
		final Nutzer value = Nutzer.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}

	@Test
	public void jsonSprache() {
		final String name = "Max Mustermann";
		final Nutzer value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(name + "@a.de", value.getMail());
		assertEquals(name, value.getName());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));

		value.getAllSprache().addAll(Arrays.stream(Sprache.values()).map(Sprache::name).collect(Collectors.toSet()));
		assertDoesNotThrow(value::verify);
		assertEquals(4, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.DE.name()));
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
		assertTrue(value.getAllSprache().contains(Sprache.FR.name()));
		assertTrue(value.getAllSprache().contains(Sprache.IT.name()));

		value.getAllSprache().clear();
		assertDoesNotThrow(value::verify);
		assertEquals(0, value.getAllSprache().size());

		value.getAllSprache().clear();
		value.getAllSprache().add(Sprache.EN.name());
		assertEquals(1, value.getAllSprache().size());
		assertTrue(value.getAllSprache().contains(Sprache.EN.name()));
	}
}
