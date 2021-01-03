package esy.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestApiResult {

    @Getter
    private final int code;

    @Getter
    private final byte[] body;

    @Getter
    private final String etag;

    public RestApiResult(@NonNull final HttpURLConnection connection) {
        this.code = code(connection);
        this.body = body(connection);
        this.etag = etag(connection);
    }

    private int code(final HttpURLConnection connection) {
        try {
            return connection.getResponseCode();
        } catch (final IOException e) {
            return 500;
        }
    }

    private byte[] body(final HttpURLConnection connection) {
        try {
            return connection.getInputStream().readAllBytes();
        } catch (final IOException e) {
            return new byte[0];
        }
    }

    private String etag(final HttpURLConnection connection) {
        final String etag = connection.getHeaderField("ETag");
        return etag == null ? "" : etag.replace("\"", "");
    }

    public <T> T toObject(@NonNull final Class<T> expectedClass) throws IOException {
        final ObjectMapper mapper = JsonMapper.configure(new ObjectMapper());
        final T value = mapper.readValue(body, expectedClass);
        // Version comes with the ETag header
        if (!etag.isBlank()) {
            final Field versionField = ReflectionUtils.findField(expectedClass, "version", Long.class);
            if (versionField != null) {
                ReflectionUtils.makeAccessible(versionField);
                ReflectionUtils.setField(versionField, value, Long.valueOf(etag));
            }
        }
        return value;
    }

    static final class CollectionModel<V> {

        @JsonProperty("content")
        @Getter
        private final List<V> content;

        CollectionModel() {
            this.content = new ArrayList<>();
        }

        CollectionModel(final Iterable<V> content) {
            this.content = new ArrayList<>();
            for (final V element : content) {
                this.content.add(element);
            }
       }
    }

    public <T> List<T> toCollection(@NonNull final Class<T> expectedClass) throws IOException {
        if (body.length < "{}".length()) {
            return Collections.emptyList();
        }
        final ObjectMapper mapper = JsonMapper.configure(new ObjectMapper());
        final JavaType type = mapper.getTypeFactory().constructParametricType(CollectionModel.class, expectedClass);
        return ((CollectionModel<T>)mapper.readValue(body, type)).getContent();
    }

    @Override
    public String toString() {
        return new String(body);
    }
}
