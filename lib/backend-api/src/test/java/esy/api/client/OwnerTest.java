package esy.api.client;

import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class OwnerTest {

	static Owner createWithName(final String name) {
		return Owner.fromJson("""
                {
                	"name":"%s",
                	"address":"Bergweg 1, 5400 Hallein",
                	"contact":"+43 660 5557683"
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
	void writeJson() {
		final var name = "Max Mustermann";
		final var value = createWithName(name);
		final var json = new JsonJpaMapper().parseJsonNode(value.writeJson());
		assertFalse(json.at("/version").isMissingNode());
		assertFalse(json.at("/id").isMissingNode());
		assertFalse(json.at("/name").isMissingNode());
		assertFalse(json.at("/address").isMissingNode());
		assertFalse(json.at("/contact").isMissingNode());
	}

	@Test
	void withId() {
		final var name = "Max Mustermann";
		final var value0 = createWithName(name);
		assertNotNull(value0.getId());
		assertNotNull(value0.getName());
		assertNotNull(value0.getAddress());
		assertNotNull(value0.getContact());
		final var value1 = value0.withId(value0.getId());
		assertSame(value0, value1);
		final var value2 = value0.withId(UUID.randomUUID());
		assertNotSame(value0, value2);
		assertTrue(value0.isEqual(value2));
	}

	@Test
	void jsonName() {
		final var name = "Mia Musterfrau";
		final var value = Owner.fromJson("""
                        {
                            "name":"%s",
                			"address":"Bergweg 1, 5400 Hallein"
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
                			"address":"Bergweg 1, 5400 Hallein"
                        }
                        """.formatted(text);
		assertThrows(IllegalArgumentException.class, () -> Owner.fromJson(json).verify());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"Bergweg 1",
			"Bergweg 1, 5400 Hallein",
			"Bergweg 1, 5400 Hallein, AT"
	})
	void jsonAddress(final String address) {
		final var value = Owner.fromJson("""
                        {
                            "name":"Jane Doe",
                			"address":"%s"
                        }
                        """.formatted(address));
		assertDoesNotThrow(value::verify);
		assertEquals(address, value.getAddress());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"",
			" ",
			"\\t",
			"\\n"
	})
	void jsonAddressConstraints(final String text) {
		final var json = """
                        {
                            "name":"John Doe",
                			"address":"%s"
                        }
                        """.formatted(text);
		assertThrows(IllegalArgumentException.class, () -> Owner.fromJson(json).verify());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"",
			"+43 666 123456",
			"me@we.com"
	})
	void jsonContact(final String contact) {
		final var value = Owner.fromJson("""
                        {
                            "name":"Jane Doe",
                			"address":"Bergweg 1, 5400 Hallein",
                			"contact":"%s"
                        }
                        """.formatted(contact));
		assertDoesNotThrow(value::verify);
		assertEquals(contact, value.getContact());
	}
}
