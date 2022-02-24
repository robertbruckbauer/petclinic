package esy.api.info;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "enum", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"art", "code"}),
        @UniqueConstraint(columnNames = {"art", "name"})
})
public final class Enum extends JsonJpaEntity<Enum> {

    /**
     * Art der Aufzählung (Diskriminitor)
     */
    @Column(name = "art")
    @Getter
    @JsonProperty
    private String art;

    /**
     * Code eines Wertes.
     */
    @Column(name = "code")
    @Getter
    @JsonProperty
    private Long code;

    /**
     * Name eines Wertes (Kurzbezeichnung, Kürzel, Abkürzung)
     */
    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    /**
     * Text eines Wertes (Langbezeichnung, Beschreibung)
     */
    @Column(name = "text")
    @Getter
    @JsonProperty
    private String text;

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
    Enum() {
        super();
        this.art = "";
        this.code = 0L;
        this.name = "";
        this.text = "";
    }

    /**
     * Erzeugt eine Instanz mit Standardwerten. Die
     * Instanz ist nicht gültig, d.h. der Aufruf von
     * {@link #verify()} ist nicht erfolgreich.
     */
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
                this.code == that.code &&
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
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        return allExtra;
    }

    public Enum setArt(@NonNull final String art) {
        this.art = art;
        return this;
    }

    public Enum setCode(@NonNull final Long code) {
        this.code = code;
        return this;
    }

    public Enum setName(@NonNull final String name) {
        this.name = name;
        return this;
    }

    public Enum setText(@NonNull final String text) {
        this.text = text;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Enum parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Enum.class);
    }
}
