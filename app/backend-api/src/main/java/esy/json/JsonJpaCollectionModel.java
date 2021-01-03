package esy.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class JsonJpaCollectionModel<V> {

    @JsonProperty("content")
    @Getter
    private final List<V> content;

    public JsonJpaCollectionModel() {
        this.content = new ArrayList<>();
    }

    public JsonJpaCollectionModel(@NonNull final Iterable<V> content) {
        this.content = new ArrayList<>();
        for (final V element : content) {
            this.content.add(element);
        }
    }
}
