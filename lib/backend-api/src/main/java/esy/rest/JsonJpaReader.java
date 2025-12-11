package esy.rest;

import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class JsonJpaReader {

    public static final TypeRef<List<String>> LIST_STRING_TYPE = new TypeRef<>() {
    };

    private final ReadContext context;

    public <T> T read(@NonNull final String path) {
        return context.read(path);
    }

    public <T> T read(@NonNull final String path, @NonNull final Class<T> type) {
        return context.read(path, type);
    }

    public <T> T read(@NonNull final String path, @NonNull final TypeRef<T> type) {
        return context.read(path, type);
    }

    public List<String> readContent(@NonNull final String path) {
        return context.read("$.content[*]." + path, LIST_STRING_TYPE);
    }
}
