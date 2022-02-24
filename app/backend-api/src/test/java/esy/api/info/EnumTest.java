package esy.api.info;

import com.fasterxml.jackson.databind.JsonNode;
import esy.json.JsonMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class EnumTest {

    Enum createWithName(final String name) {
        final String json = "{" +
                "\"art\": \"QUELLE\"," +
                "\"name\": \"" + name + "\"," +
                "\"code\": \"2\"," +
                "\"text\": \"A " + name + "\"" +
                "}";
        return Enum.parseJson(json);
    }

    @Test
    void equalsHashcodeToString() {
        final String name = "JIRA";
        final Enum value = createWithName(name);
        // Identisches Objekt
        assertEquals(value, value);
        assertTrue(value.isEqual(value));
        assertEquals(value.hashCode(), value.hashCode());
        assertEquals(value.toString(), value.toString());
        // Gleiche UUID
        final Enum clone = Enum.parseJson(value.writeJson());
        assertTrue(clone.isEqual(value));
        assertEquals(clone.hashCode(), value.hashCode());
        assertEquals(clone.toString(), value.toString());
        // Gleicher Text
        assertNotEquals(createWithName(name), value);
        assertTrue(value.isEqual(createWithName(name)));
        assertNotEquals(createWithName(name).hashCode(), value.hashCode());
        assertNotEquals(createWithName(name).toString(), value.toString());
        // Anderer Text
        final Enum other = createWithName("ARIJ");
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
        final String name = "JIRA";
        final Enum value0 = createWithName(name);
        final Enum value1 = value0.withId(value0.getId());
        assertSame(value0, value1);
        final Enum value2 = value0.withId(UUID.randomUUID());
        assertNotSame(value0, value2);
        assertTrue(value0.isEqual(value2));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"version\": \"1\"",
            "\"created\": \"2019-04-22\"",
            "\"garbage\": \"value\""
    })
    void jsonGarbage(final String line) {
        final String name = "JIRA";
        final String json = "{" +
                "\"art\": \"QUELLE\"," +
                "\"name\": \"" + name + "\"," +
                "\"code\": \"2\"," +
                "\"text\": \"A " + name + "\"," +
                line +
                "}";
        final Enum value = Enum.parseJson(json);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
        assertEquals("QUELLE", value.getArt());
        assertEquals(2L, value.getCode());
        assertEquals(name, value.getName());
        assertEquals("A " + name, value.getText());
    }

    @Test
    void json() {
        final String name = "JIRA";
        final Enum value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertNotNull(value.getId());
        assertEquals("QUELLE", value.getArt());
        assertEquals(2L, value.getCode());
        assertEquals(name, value.getName());
        assertEquals("A " + name, value.getText());

        final JsonNode json = new JsonMapper().parseJsonNode(value.writeJson());
        assertEquals(0, json.at("/version").asLong());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"text\": \"\"}",
            "{\"text\": \"\\t\"}",
            "{\"text\": \" \"}"
    })
    void jsonConstraints(final String json) {
        final Enum value = Enum.parseJson(json);
        assertThrows(IllegalArgumentException.class, value::verify);
        assertTrue(value.getArt().isBlank());
        assertEquals(0L, value.getCode());
        assertTrue(value.getName().isBlank());
        assertTrue(value.getText().isBlank());
    }

    @Test
    void jsonArt() {
        final String name = "JIRA";
        final Enum value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertEquals("QUELLE", value.getArt());

        assertThrows(NullPointerException.class, () -> value.setCode(null));

        value.setArt("ELLEUQ");
        assertDoesNotThrow(value::verify);
        assertNotEquals("QUELLE", value.getArt());

        value.setArt("QUELLE");
        assertDoesNotThrow(value::verify);
        assertEquals("QUELLE", value.getArt());
    }

    @Test
    void jsonCode() {
        final String name = "JIRA";
        final Enum value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertEquals(2L, value.getCode());

        assertThrows(NullPointerException.class, () -> value.setCode(null));

        value.setCode(3L);
        assertDoesNotThrow(value::verify);
        assertNotEquals(2L, value.getCode());

        value.setCode(2L);
        assertDoesNotThrow(value::verify);
        assertEquals(2L, value.getCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Das ist ein Text.",
            "TEXT",
            "Text",
            "text",
            "Spaß",
            "Ärger",
            "Öfen",
            "Übel"
    })
    void jsonName(final String name) {
        final Enum value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertEquals(name, value.getName());

        assertThrows(NullPointerException.class, () -> value.setName(null));

        value.setName("X" + name);
        assertDoesNotThrow(value::verify);
        assertNotEquals(name, value.getName());

        value.setName(name);
        assertDoesNotThrow(value::verify);
        assertEquals(name, value.getName());
    }

    @Test
    void jsonText() {
        final String name = "JIRA";
        final Enum value = createWithName(name);
        assertDoesNotThrow(value::verify);
        assertEquals("A " + name, value.getText());

        assertThrows(NullPointerException.class, () -> value.setText(null));

        value.setText("ARIJ");
        assertDoesNotThrow(value::verify);
        assertEquals("ARIJ", value.getText());

        value.setText(name);
        assertDoesNotThrow(value::verify);
        assertEquals(name, value.getText());
    }
}
