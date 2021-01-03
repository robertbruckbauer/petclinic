package esy.api.plan;

import esy.api.team.Nutzer;
import esy.api.team.Sprache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProjektTest {

	Projekt createWithName(final String name) {
		return Projekt.parseJson("{" +
				"\"version\": \"1\"," +
				"\"name\": \"" + name + "\"," +
				"\"aktiv\": \"false\"," +
				"\"sprache\": \"EN\"," +
				"\"besitzer\": null," +
				"\"allMitglied\": []" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String name = "Projekt A";
		final Projekt value = createWithName(name);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Projekt clone = createWithName(name);
		assertNotSame(clone, value);
		assertNotEquals(clone, value);
		assertTrue(clone.isEqual(value));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Projekt other = createWithName("X" + name);
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
		final String name = "Projekt A";
		final Projekt value0 = createWithName(name);
		final Projekt value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Projekt value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String name = "Projekt A";
		final Projekt value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(name, value.getName());
		assertFalse(value.isAktiv());
		assertEquals(Sprache.EN.name(), value.getSprache());
		assertNull(value.getBesitzer());
		assertEquals(0, value.getAllMitglied().size());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"name\": \"\"}",
			"{\"aktiv\": \"false\"}"
	})
	void jsonConstraints(final String json) {
		final Projekt value = Projekt.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}

	@Test
	public void jsonBesitzer() {
		final String name = "Projekt A";
		final Projekt value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertNull(value.getBesitzer());

		final Nutzer nutzer = Nutzer.parseJson("{" +
				"\"mail\": \"Max.Mustermann@a.de\"," +
				"\"name\": \"Max Mustermann\"" +
				"}");
		value.setBesitzer(nutzer);
		assertDoesNotThrow(value::verify);
		assertSame(nutzer, value.getBesitzer());

		value.setBesitzer(null);
		assertDoesNotThrow(value::verify);
		assertNull(value.getBesitzer());
	}

	@Test
	public void jsonMitglied() {
		final String name = "Projekt A";
		final Projekt value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());
		assertEquals(0, value.getAllMitglied().size());

		final Nutzer nutzer = Nutzer.parseJson("{" +
				"\"mail\": \"Max.Mustermann@a.de\"," +
				"\"name\": \"Max Mustermann\"" +
				"}");
		value.addMitglied(nutzer);
		assertDoesNotThrow(value::verify);
		assertEquals(1, value.getAllMitglied().size());
		assertEquals(1, value.getAllMitglied().stream()
				.filter(e -> e.getId().equals(nutzer.getId()))
				.count());

		value.getAllMitglied().clear();
		assertDoesNotThrow(value::verify);
		assertEquals(0, value.getAllMitglied().size());
	}

	@Test
	public void jsonAufgabe() {
		final String name = "Projekt A";
		final Projekt value = createWithName(name);
		assertDoesNotThrow(value::verify);
		assertEquals(name, value.getName());
		assertEquals(0, value.getAllMitglied().size());

		final Aufgabe aufgabe = Aufgabe.parseJson("{" +
				"\"version\": \"1\"," +
				"\"text\": \"Lorem ipsum.\"," +
				"\"aktiv\": \"false\"" +
				"}");
		value.addAufgabe(aufgabe);
		assertDoesNotThrow(value::verify);
		assertEquals(1, value.getAllAufgabe().size());
		assertEquals(1, value.getAllAufgabe().stream()
				.filter(e -> e.getId().equals(aufgabe.getId()))
				.count());

		value.getAllAufgabe().clear();
		assertDoesNotThrow(value::verify);
		assertEquals(0, value.getAllAufgabe().size());
	}
}
