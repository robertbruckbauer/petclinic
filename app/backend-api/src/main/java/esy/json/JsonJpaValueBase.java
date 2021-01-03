package esy.json;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "dataId")
public abstract class JsonJpaValueBase<SELF extends JsonJpaValueBase<?>> implements JsonWithDataId<SELF> {

    /**
     * Aktuelle Version der Daten.
     */
    @Version
    @Column(name = "version", nullable = false)
    @Getter
    @JsonProperty
    private final Long version;

    /**
     * Eindeutige ID der Daten.
     */
    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @JsonProperty
    private final UUID dataId;

    /**
     * {@code TRUE} zeigt an, dass die Daten in einer Datenbank
     * dauerhaft gespeichert wurden. Das bedeutet, dass #dataId
     * eine gültige ID und primärer Schlüssel des Datensatzes ist.
     */
    @Transient
    @JsonIgnore
    private boolean persisted = false;

    protected JsonJpaValueBase() {
        this.version = 0L;
        this.dataId = UUID.randomUUID();
    }

    protected JsonJpaValueBase(@NonNull final Long version,
                               @NonNull final UUID dataId) {
        this.version = version;
        this.dataId = dataId;
    }

    /**
     * Prüft die Eigenschaften dieser Instanz und wirft eine
     * {@link IllegalArgumentException}, wenn etwas nicht in
     * Ordnung ist.
     *
     * @return diese Instanz
     */
    public abstract SELF verify();

    @Override
    public final int hashCode() {
        return Objects.hash(
                this.version,
                this.dataId);
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
        final SELF that = (SELF) any;
        return Objects.equals(this.version, that.getVersion()) &&
                Objects.equals(this.dataId, that.getDataId()) &&
                this.isEqual(that);
    }

    /**
     * Liefert {@code TRUE}, wenn alle Eigenschaften dieser
     * Instanz und einer anderen Instanz gleich sind.
     *
     * Wichtig: Bitte wegen dem Einsatz von O/R-Mappern die
     * Methoden {@link Object#equals(Object)} und und
     * {@link Object#hashCode()} nicht implementieren.
     *
     * @param that Vergleichsobjekt
     * @return {@code TRUE} bei gleichen Eigenschaften, sonst {@code FALSE}
     */
    public abstract boolean isEqual(final SELF that);

    /**
     * Erzeugt einen lesbaren Text für dieses Objekt mit einer
     * komma-separierten Liste der eindeutigen Attributen dieser
     * Instanz.
     *
     * @return Komma-separierte Liste
     */
    @Override
    public String toString() {
        return "version:" + version + ",id:" + dataId;
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
