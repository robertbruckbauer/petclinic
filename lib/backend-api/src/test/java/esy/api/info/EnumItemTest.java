package esy.api.info;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
class EnumItemTest {

    Enum createWithName(final String name) {
        return Enum.parseJson("""
                {
                    "art":"QUELLE",
                    "name":"%1$s",
                    "code":"2",
                    "text":"A %1$s"
                }
                """.formatted(name));
    }

    @Test
    void equalsHashcodeToString() {
        final var name = "JIRA";
        final var value = EnumItem.fromValue(createWithName(name));
        // Identisches Objekt
        assertEquals(value, value);
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiches Objekt
        final var clone = EnumItem.fromValue(createWithName(name));
        assertNotSame(clone, value);
        assertEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Anderes Objekt
        final var other = EnumItem.fromValue(createWithName("ARIJ"));
        assertNotSame(other, value);
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
        final var item = EnumItem.fromValue(null);
        assertNull(item.getValue());
        assertNull(item.getName());
        assertEquals("", item.getText());
        assertEquals(0L, item.getCode());
        assertFalse(item.isCreate());
        assertFalse(item.isUpdate());
        assertTrue(item.isDelete());
    }

    @Test
    void ofValue() {
        final var name = "JIRA";
        final var value = createWithName(name);
        final var item = EnumItem.fromValue(value);
        assertEquals(name, item.getValue());
        assertEquals(value.getName(), item.getName());
        assertEquals(value.getText(), item.getText());
        assertEquals(value.getCode(), item.getCode());
        assertFalse(item.isCreate());
        assertTrue(item.isUpdate());
        assertFalse(item.isDelete());
    }
}
