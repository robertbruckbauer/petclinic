package esy.api.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
public class PetItem implements JsonJpaItem<UUID> {

    @Transient
    @Getter
    @JsonProperty
    private final UUID value;

    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;

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
