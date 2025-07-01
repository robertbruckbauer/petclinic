package esy.api.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class PetItemTest {

    Pet createWithName(final String name) {
        return Pet.parseJson("""
                {
                    "name":"%s",
                    "born":"2021-04-22",
                    "species":"Cat"
                }
                """.formatted(name));
    }

    @Test
    void equalsHashcodeToString() {
        final var name = "Tom";
        final var value = PetItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final var clone = PetItem.fromValue(createWithName(name));
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final var other = PetItem.fromValue(createWithName("X" + name));
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
        final var item = PetItem.fromValue(null);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final var name = "Tom";
        final var value = createWithName(name);
        final var item = PetItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals("Cat 'Tom'", item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
