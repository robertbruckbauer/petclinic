package esy.api.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class OwnerItemTest {

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
        final OwnerItem value = OwnerItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final OwnerItem clone = OwnerItem.fromValue(createWithName(name));
        assertNotSame(value, clone);
        assertNotEquals(clone, value);
        assertNotEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final OwnerItem other = OwnerItem.fromValue(createWithName("X" + name));
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
        final Owner value = null;
        final OwnerItem item = OwnerItem.fromValue(value);
        assertNull(item.getValue());
        assertTrue(item.getText().isEmpty());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final String name = "Max Mustermann";
        final Owner value = createWithName(name);
        final OwnerItem item = OwnerItem.fromValue(value);
        assertEquals(value.getId(), item.getValue());
        assertEquals(value.getName(), item.getText());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
