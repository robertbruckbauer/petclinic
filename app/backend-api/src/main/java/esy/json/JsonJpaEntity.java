package esy.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class JsonJpaEntity<T extends JsonJpaEntity<?>> implements JsonWithId<T> {

    /**
     * Aktuelle Version der Daten.
     */
    @Version
    @Column(name = "version")
    @Getter
    @JsonProperty
    private final Long version;

    /**
     * Eindeutige ID der Daten.
     */
    @Id
    @Column(name = "id")
    @Getter
    @JsonProperty
    private final UUID id;

    /**
     * {@code TRUE} zeigt an, dass die Daten in einer Datenbank
     * dauerhaft gespeichert wurden. Das bedeutet, dass ein
     * gültiger primärer Schlüssel existiert.
     */
    @Transient
    @JsonIgnore
    private boolean persisted = false;

    protected JsonJpaEntity() {
        this.version = 0L;
        this.id = UUID.randomUUID();
    }

    protected JsonJpaEntity(@NonNull final Long version,
                            @NonNull final UUID id) {
        this.version = version;
        this.id = id;
    }

    /**
     * Prüft die Eigenschaften dieser Instanz und wirft eine
     * {@link IllegalArgumentException}, wenn etwas nicht in
     * Ordnung ist.
     *
     * @return diese Instanz
     */
    public abstract T verify();

    @Override
    public final int hashCode() {
        return Objects.hash(
                this.version,
                this.id);
    }

    @Override
    public final boolean equals(final Object any) {
        if (this == any) {
            return true;
        }
        if (any == null) {
            return false;
        }
        if (!getClass().equals(any.getClass())) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final var that = (T) any;
        return Objects.equals(this.getVersion(), that.getVersion()) &&
                Objects.equals(this.getId(), that.getId());
    }

    /**
     * Liefert {@code TRUE}, wenn alle Eigenschaften dieser
     * Instanz und einer anderen Instanz gleich sind.
     *
     * Wichtig: Bitte wegen des Einsatzes von O/R-Mappern
     * {@link Object#equals(Object)} und
     * {@link Object#hashCode()} nicht implementieren.
     *
     * @param that Vergleichsobjekt
     * @return {@code TRUE} bei gleichen Eigenschaften, sonst {@code FALSE}
     */
    public abstract boolean isEqual(final T that);

    /**
     * Erzeugt einen lesbaren Text für dieses Objekt mit einer
     * komma-separierten Liste eindeutiger Attribute dieser
     * Instanz.
     *
     * @return Komma-separierte Liste
     */
    @Override
    public String toString() {
        return "version:" + version + ",id:" + id;
    }

    @PrePersist
    @PostLoad
    private void setPersisted() {
        persisted = true;
    }

    @JsonIgnore
    public final boolean isPersisted() {
        return persisted;
    }

    @JsonIgnore
    public final boolean isNew() {
        return version == 0L;
    }

    /**
     * Erzeugt einen lesbaren Text für dieses Objekt mit einer
     * JSON-Struktur aller Attribute dieser Instanz.
     *
     * @return JSON-Struktur
     */
    public abstract String writeJson();
}
