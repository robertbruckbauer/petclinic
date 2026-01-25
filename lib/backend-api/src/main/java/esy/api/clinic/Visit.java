package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.client.OwnerItem;
import esy.api.client.Pet;
import esy.api.client.PetItem;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "visit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"date","pet_id"})
})
public final class Visit extends JsonJpaEntity<Visit> {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final String TIME_PATTERN = "HH:mm";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

    // tag::properties[]
    @NotNull
    @Column(name = "date", columnDefinition = "DATE")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate date;

    @Column(name = "time", columnDefinition = "TIME")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = TIME_PATTERN)
    private LocalTime time;

    @Column(name = "text")
    @Getter
    @JsonProperty
    private String text;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "pet_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Pet pet;

    @ManyToOne(
            fetch = FetchType.EAGER,
            optional = true)
    @JoinColumn(name = "vet_id", referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Vet vet;
    // end::properties[]

    Visit() {
        super();
        this.date = LocalDate.of(2000, 1, 1);
        this.time = null;
        this.text = "";
        this.pet = null;
        this.vet = null;
    }

    Visit(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.date = LocalDate.of(2000, 1, 1);
        this.time = null;
        this.text = "";
        this.pet = null;
        this.vet = null;
    }

    @Override
    public String toString() {
        return super.toString() + ",date='" + date + "'";
    }

    @Override
    public boolean isEqual(final Visit that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return Objects.equals(this.date, that.date) &&
                Objects.equals(this.time, that.time) &&
                Objects.equals(this.text, that.text) &&
                Objects.equals(this.pet, that.pet) &&
                Objects.equals(this.vet, that.vet);
    }

    @Override
    public Visit verify() {
        return this;
    }

    @Override
    public Visit withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final var value = new Visit(getVersion(), id);
        value.date = this.date;
        value.time = this.time;
        value.text = this.text;
        value.pet = this.pet;
        value.vet = this.vet;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final var allExtra = new HashMap<String, Object>();
        allExtra.put("version", getVersion());
        allExtra.put("ownerItem", OwnerItem.fromValue(pet));
        allExtra.put("petItem", PetItem.fromValue(pet));
        allExtra.put("vetItem", VetItem.fromValue(vet));
        return allExtra;
    }

    @JsonIgnore
    public Visit setPet(@NonNull final Pet pet) {
        this.pet = pet;
        return this;
    }

    @JsonIgnore
    public Visit setVet(@NonNull final Vet vet) {
        this.vet = vet;
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonJpaMapper().writeJson(this);
    }

    public static Visit fromJson(@NonNull final String json) {
        return new JsonJpaMapper().parseJson(json, Visit.class);
    }
}
