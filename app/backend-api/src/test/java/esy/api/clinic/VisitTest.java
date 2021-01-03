package esy.api.clinic;

import esy.api.client.Pet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class VisitTest {

    Visit createWithName(final String name) {
        return Visit.parseJson("{" +
                        "\"date\":\"2021-04-22\"," +
                        "\"text\":\"Lorem ipsum.\"" +
                        "}")
                .setPet(Pet.parseJson("{" +
                        "\"id\":\"deadbeef-dead-beef-dead-deadbeefdead\"," +
                        "\"name\":\"" + name + "\"," +
                        "\"born\":\"2020-12-24\"," +
                        "\"species\":\"Cat\"" +
                        "}"))
                .setVet(Vet.parseJson("{" +
                        "\"id\":\"deadbeef-dead-beef-dead-deadbeefdead\"," +
                        "\"name\":\"John Cleese\"" +
                        "}"));
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Tom";
        final Visit value = createWithName(name);
        // Identisches Objekt
        assertEquals(value, value);
        assertTrue(value.isEqual(value));
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final Visit clone = createWithName(name);
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertTrue(value.isEqual(clone));
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertNotEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final Visit other = createWithName("X" + name);
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
        final Visit value0 = createWithName(name);
        final Visit value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final Visit value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @Test
    void json() {
        final String name = "Tom";
        final Visit value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
        assertEquals(2021, value.getDate().getYear());
        assertEquals(Month.APRIL, value.getDate().getMonth());
        assertEquals(22, value.getDate().getDayOfMonth());
        assertFalse(value.getText().isBlank());
    }
}
