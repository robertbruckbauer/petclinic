package esy.api.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class EnumItem implements JsonJpaItem<String>  {

    @Column(name = "name")
    @Getter
    @JsonProperty
    private final String name;

    @Column(name = "text")
    @Getter
    @JsonProperty
    private final String text;

    @Column(name = "code")
    @Getter
    @JsonProperty
    private final Long code;

    public EnumItem() {
        this.name = null;
        this.text = "";
        this.code = 0L;
    }

    public EnumItem(@NonNull final Enum value) {
        this.name = value.getName();
        this.text = value.getText();
        this.code = value.getCode();
    }

    @Override
    @JsonProperty
    public String getValue() {
        return name;
    }

    @Override
    public String toString() {
        return text;
    }

    public static EnumItem fromValue(final Enum value) {
        if (value != null) {
            return new EnumItem(value);
        } else {
            return new EnumItem();
        }
    }
}
