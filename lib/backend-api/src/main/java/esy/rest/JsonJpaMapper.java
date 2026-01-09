package esy.rest;

import lombok.NonNull;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JsonJpaMapper {

    // tag::configure[]
    public static JsonMapper.Builder configure(@NonNull final JsonMapper.Builder builder) {
        return builder
                .defaultMergeable(false) // <1>
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // <2>
    }
    // end::configure[]

    /**
     * Writes a JSON structure for a value object.
     *
     * @param value value object
     * @return JSON structure
     */
    public <T> String writeJson(@NonNull final T value) {
        try {
            final var builder = JsonMapper.builder();
            final var mapper = configure(builder).build();
            return mapper.writeValueAsString(value);
        } catch (final JacksonException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Parses a JSON structure and creates a {@link JsonNode}.
     *
     * @param json JSON structure
     * @return {@link JsonNode}
     */
    public JsonNode parseJsonNode(@NonNull final String json) {
        if (json.isBlank()) {
            throw new IllegalArgumentException("json structure is blank");
        }
        try {
            final var builder = JsonMapper.builder();
            final var mapper = configure(builder).build();
            return mapper.readTree(json);
        } catch (final JacksonException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Parses a JSON structure and creates a {@link Map}.
     *
     * @param json JSON structure
     * @return {@link Map}
     */
    public Map<String, Object> parseJsonMap(@NonNull final String json) {
        if (json.isBlank()) {
            throw new IllegalArgumentException("json structure is blank");
        }
        try {
            final var builder = JsonMapper.builder();
            final var mapper = configure(builder).build();
            final var typeRef = new TypeReference<Map<String, Object>>() {};
            return mapper.readValue(json, typeRef);
        } catch (final JacksonException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Parses a JSON structure and creates a value object.
     *
     * @param json JSON structure
     * @param type value type
     * @param patcher {@link Consumer} for patches
     * @return value object
     */
    @SafeVarargs
    public final <T> T parseJson(@NonNull final String json, @NonNull final Class<T> type, @NonNull final Consumer<T>... patcher) {
        try {
            final var builder = JsonMapper.builder();
            final var mapper = configure(builder).build();
            final var value = mapper.readValue(json, type);
            Stream.of(patcher).forEach(p -> p.accept(value));
            return value;
        } catch (final JacksonException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }

    /**
     * Parses a JSON structure and creates a list of value objects.
     *
     * @param json JSON structure
     * @param type value type
     * @return list of value objects
     */
    @SafeVarargs
    @SuppressWarnings("unchecked") // no other solution
    public final <T> List<T> parseJsonContent(@NonNull final String json, @NonNull final Class<T> type, @NonNull final Consumer<T>... patcher) {
        try {
            final var builder = JsonMapper.builder();
            final var mapper = configure(builder).build();
            final var mappedType = mapper.getTypeFactory().constructParametricType(JsonJpaCollectionModel.class, type);
            final var allValue = ((JsonJpaCollectionModel<T>) mapper.readValue(json, mappedType)).getContent();
            Stream.of(patcher).forEach(p -> allValue.forEach(value -> p.accept(value)));
            return allValue;
        } catch (final JacksonException e) {
            throw new IllegalArgumentException(e.toString(), e);
        }
    }
}
