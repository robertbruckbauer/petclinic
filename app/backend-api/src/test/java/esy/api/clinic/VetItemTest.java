package esy.api.clinic;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class VetItemTest {

    Vet createWithName(final String name) {
        return Vet.parseJson("""
                {
                    "name":"%s"
                }
                """.formatted(name));
    }

    @Test
    void equalsHashcodeToString() {
        final var name = "Tom";
        final var value = VetItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final var clone = VetItem.fromValue(createWithName(name));
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final var other = VetItem.fromValue(createWithName("X" + name));
        assertNotSame(value, other);
        assertNotEquals(other, value);
        assertNotEquals(other.hashCode(), value.hashCode());
        assertNotEquals(other.toString(), value.toString());
        // Kein Objekt
        assertNotEquals(value, null);
        // Falsches Objekt
        assertNotEquals(this, value);
    }

    @Test
    void ofNull() {
        final var item = VetItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final var name = "Max Mustermann";
        final var value = createWithName(name);
        final var item = VetItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals(value.getName(), item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
