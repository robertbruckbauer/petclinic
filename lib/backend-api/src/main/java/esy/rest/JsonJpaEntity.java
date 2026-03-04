package esy.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class JsonJpaEntity<T extends JsonJpaEntity<?>> implements JsonJpaWithId<T> {

    @Version
    @Column(name = "version", nullable = false)
    @Getter
    @JsonIgnore // -> extraJson
    private final Long version;

    @Id
    @Column(name = "id", nullable = false)
    @Getter
    @JsonProperty
    private final UUID id;

    /**
     * {@code TRUE} indicates that the data has been permanently
     * stored in a database. This means that a valid primary key
     * exists.
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
     * Validates the properties of this instance and throws an
     * {@link IllegalArgumentException} if anything is invalid.
     *
     * @return this instance
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
     * Returns {@code TRUE} if all properties of this instance
     * and another instance are equal.
     *
     * Important: Do not use {@link Object#equals(Object)} and
     * {@link Object#hashCode()} here due to the use of the
     * JPA provider.
     *
     * @param that the object to compare with
     * @return {@code TRUE} or {@code FALSE}
     */
    public abstract boolean isEqual(final T that);

    /**
     * Returns a human-readable string representation of this
     * object as a comma-separated list of some properties.
     *
     * @return comma-separated list
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
     * Returns a human-readable string representation of this
     * object as a JSON structure of all its properties.
     *
     * @return JSON structure
     */
    public abstract String writeJson();
}
