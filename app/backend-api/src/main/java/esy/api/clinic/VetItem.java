package esy.api.clinic;

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
public class VetItem implements JsonJpaItem<UUID> {

    @Transient
    @Getter
    @JsonProperty
    private final UUID value;

    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;

    private VetItem() {
        this.value = null;
        this.text = "";
    }

    private VetItem(@NonNull final Vet value) {
        this.value = value.getId();
        this.text = value.getName();
    }

    @Override
    public String toString() {
        return text;
    }

    public static VetItem fromValue(final Vet value) {
        if (value != null) {
            return new VetItem(value);
        } else {
            return new VetItem();
        }
    }
}
