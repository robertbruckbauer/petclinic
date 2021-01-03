package esy.api.clinic;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class VetItemTest {

    Vet createWithName(final String name) {
        return Vet.parseJson("{" +
                "\"name\":\"" + name + "\"" +
                "}");
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "Tom";
        final VetItem value = VetItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final VetItem clone = VetItem.fromValue(createWithName(name));
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final VetItem other = VetItem.fromValue(createWithName("X" + name));
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
        final Vet value = null;
        final VetItem item = VetItem.fromValue(value);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Max Mustermann";
        final Vet value = createWithName(name);
        final VetItem item = VetItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals(value.getName(), item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
