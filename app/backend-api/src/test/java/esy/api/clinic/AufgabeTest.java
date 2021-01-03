package esy.api.plan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AufgabeTest {

	static Aufgabe createWithText(final String text) {
		return Aufgabe.parseJson("{" +
				"\"version\": \"1\"," +
				"\"text\": \"" + text + "\"," +
				"\"aktiv\": \"false\"" +
				"}");
	}

	@Test
	void equalsHashcodeToString() {
		final String text = "Aufgabe A";
		final Aufgabe value = createWithText(text);
		// Identisches Objekt
		assertEquals(value, value);
		assertTrue(value.isEqual(value));
		assertEquals(value.hashCode(), value.hashCode());
		assertEquals(value.toString(), value.toString());
		// Gleiches Objekt
		final Aufgabe clone = createWithText(text);
		assertNotSame(clone, value);
		assertTrue(clone.isEqual(value));
		assertNotEquals(clone.hashCode(), value.hashCode());
		assertNotEquals(clone.toString(), value.toString());
		// Anderes Objekt
		final Aufgabe other = createWithText("X" + text);
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
		final String text = "Aufgabe A";
		final Aufgabe value0 = createWithText(text);
		final Aufgabe value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final Aufgabe value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void json() {
		final String text = "Aufgabe A";
		final Aufgabe value = createWithText(text);
		assertDoesNotThrow(value::verify);
		assertEquals(1L, value.getVersion());
		assertNotNull(value.getId());
		assertEquals(text, value.getText());
		assertFalse(value.isAktiv());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"{}",
			"{\"text\": \"\"}",
			"{\"aktiv\": \"false\"}"
	})
	void jsonConstraints(final String json) {
		final Aufgabe value = Aufgabe.parseJson(json);
		assertThrows(IllegalArgumentException.class, value::verify);
	}

	@Test
	public void jsonProjekt() {
		final String text = "Aufgabe A";
		final Aufgabe value = createWithText(text);
		assertDoesNotThrow(value::verify);
		assertNull(value.getProjekt());

		final Projekt projekt = Projekt.parseJson("{" +
				"\"name\": \"Projekt A\"" +
				"}");
		value.setProjekt(projekt);
		assertDoesNotThrow(value::verify);
		assertSame(projekt, value.getProjekt());

		value.setProjekt(null);
		assertDoesNotThrow(value::verify);
		assertNull(value.getProjekt());
	}
}
