package esy.api.info;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Pflegbare Aufzählung.
 */
@Entity
@Table(name = "enum", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"art", "code"}),
        @UniqueConstraint(columnNames = {"art", "name"})
})
public final class Enum extends JsonJpaEntity<Enum> {

    // tag::properties[]
    @NotBlank
    @Column(name = "art")
    @Getter
    @JsonProperty
    private String art;

    @PositiveOrZero
    @Column(name = "code")
    @Getter
    @JsonProperty
    private Long code;

    @NotBlank
    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    @NotBlank
    @Column(name = "text")
    @Getter
    @JsonProperty
    private String text;
    // end::properties[]

    Enum() {
        super();
        this.art = "";
        this.code = 0L;
        this.name = "";
        this.text = "";
    }

    Enum(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.art = "";
        this.code = 0L;
        this.name = "";
        this.text = "";
    }

    @Override
    public String toString() {
        return super.toString() + ",art='" + art + "'" + ",text='" + text + "'";
    }

    @Override
    public boolean isEqual(final Enum that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.art.equals(that.art) &&
                this.code.equals(that.code) &&
                this.name.equals(that.name) &&
                this.text.equals(that.text);
    }

    @Override
    public Enum verify() {
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("text is blank");
        }
        return this;
    }

    @Override
    public Enum withId(@NonNull final UUID id) {
        if (getId().equals(id)) {
            return this;
        }
        final Enum value = new Enum(getVersion(), id);
        value.art = this.art;
        value.code = this.code;
        value.name = this.name;
        value.text = this.text;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final var allExtra = new HashMap<String, Object>();
        allExtra.put("version", getVersion());
        return allExtra;
    }

    @JsonIgnore
    public Enum setArt(@NonNull final String art) {
        this.art = art;
        return this;
    }

    @JsonIgnore
    public Enum setCode(@NonNull final Long code) {
        this.code = code;
        return this;
    }

    @JsonIgnore
    public Enum setName(@NonNull final String name) {
        this.name = name;
        return this;
    }

    @JsonIgnore
    public Enum setText(@NonNull final String text) {
        this.text = text;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonJpaMapper().writeJson(this);
    }

    public static Enum parseJson(@NonNull final String json) {
        return new JsonJpaMapper().parseJson(json, Enum.class);
    }
}
