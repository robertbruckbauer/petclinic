package esy.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JsonMapper {

    // tag::configure[]
    public static ObjectMapper configure(@NonNull final ObjectMapper mapper) {
        return mapper
                .registerModule(new Jdk8Module()) // <1>
                .registerModule(new JavaTimeModule()) // <2>
                .setDefaultMergeable(false) // <3>
                .setSerializationInclusion(JsonInclude.Include.NON_NULL) // <4>
                .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true) // <5>
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false) // <6>
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // <7>
    }
    // end::configure[]

    /**
     * Erzeugt eine JSON-Struktur für ein Value-Objekt.
     *
     * @param value Value-Objekt
     * @return JSON
     */
    public <T> String writeJson(@NonNull final T value) {
        try {
            final var mapper = configure(new ObjectMapper());
            return mapper.writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Erzeugt einen {@link JsonNode} mit allen Elementen einer JSON-Struktur.
     *
     * @param json JSON
     * @return {@link JsonNode}
     */
    public JsonNode parseJsonNode(@NonNull final String json) {
        if (json.isBlank()) {
            throw new IllegalArgumentException("json structure is blank");
        }
        try {
            final var mapper = configure(new ObjectMapper());
            return mapper.readTree(json);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Erzeugt eine {@link Map} mit allen Elementen einer JSON-Struktur.
     *
     * @param json JSON
     * @return {@link Map}
     */
    public Map<String, Object> parseJsonMap(@NonNull final String json) {
        if (json.isBlank()) {
            throw new IllegalArgumentException("json structure is blank");
        }
        try {
            final var mapper = configure(new ObjectMapper());
            final var typeRef = new TypeReference<Map<String, Object>>() {};
            return mapper.readValue(json, typeRef);
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Erzeugt ein Value-Objekt aus einer JSON-Struktur.
     *
     * @param json JSON
     * @param type Value-Klasse
     * @return Value-Objekt
     */
    @SafeVarargs
    public final <T> T parseJson(@NonNull final String json, @NonNull final Class<T> type, @NonNull final Consumer<T>... patcher) {
        try {
            final var mapper = configure(new ObjectMapper());
            final var value = mapper.readValue(json, type);
            Stream.of(patcher).forEach(p -> p.accept(value));
            return value;
        } catch (final JsonProcessingException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

}
