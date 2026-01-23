package esy.api.basis;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ping", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class Ping extends JsonJpaEntity<Ping> {

    public static final String TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSSSSSSSS]";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    @Column(name = "at", columnDefinition = "TIMESTAMP")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalDateTime at;

    Ping() {
        super();
        this.at = LocalDateTime.now();
    }

    Ping(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.at = LocalDateTime.now();
    }

    @Override
    public boolean isEqual(final Ping that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return Objects.equals(this.at, that.at);
    }

    @Override
    public Ping verify() {
        return this;
    }

    @Override
    public Ping withId(@NonNull final UUID id) {
        if (getId().equals(id)) {
            return this;
        }
        final var value = new Ping(getVersion(), id);
        value.at = this.at;
        return value;
    }

    @JsonIgnore
    public Ping touch() {
        this.at = LocalDateTime.now();
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonJpaMapper().writeJson(this);
    }

    public static Ping fromJson(@NonNull final String json) {
        return new JsonJpaMapper().parseJson(json, Ping.class);
    }

    public static Ping fromId(@NonNull final UUID id) {
        return new Ping(0L, id);
    }
}
