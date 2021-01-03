package esy.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import lombok.NonNull;

public class JsonMapper {

    public static ObjectMapper configure(@NonNull final ObjectMapper mapper) {
        return mapper.registerModule(new Jdk8Module())
                .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setDefaultMergeable(true);
    }

    /**
     * Erzeugt eine JSON-Datenstruktur für ein Datenobjekt.
     *
     * @param value Datenobjekt
     * @return JSON-Datenstruktur
     */
    public <T> String writeJson(@NonNull final T value) {
        try {
            return configure(new ObjectMapper()).writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            // may not occur
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Erzeugt ein Datenobjekt aus einer JSON-Datenstruktur.
     *
     * @param json JSON-Datenstruktur
     * @return Datenobjekt
     */
    public <T> T parseJson(@NonNull final String json, @NonNull final Class<T> type) {
        try {
            return configure(new ObjectMapper()).readValue(json, type);
        } catch (final JsonProcessingException e) {
            // may not occur
            throw new IllegalArgumentException(e.toString(), e);
        }
    }
}
