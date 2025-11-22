package esy.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
public class PetItem implements JsonJpaItem<UUID> {

    // tag::properties[]
    @Transient
    @Getter
    @JsonProperty
    private final UUID value;

    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;
    // end::properties[]

    private PetItem() {
        this.value = null;
        this.text = "";
    }

    private PetItem(@NonNull final Pet value) {
        this.value = value.getId();
        this.text = value.getSpecies() + " '" + value.getName() + "'";
    }

    @Override
    public String toString() {
        return text;
    }

    public static PetItem fromValue(final Pet value) {
        if (value != null) {
            return new PetItem(value);
        } else {
            return new PetItem();
        }
    }
}
