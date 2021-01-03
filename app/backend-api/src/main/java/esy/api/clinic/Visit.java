package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.api.client.OwnerItem;
import esy.api.client.Pet;
import esy.api.client.PetItem;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDate;
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

    @Column(name = "date")
    @Getter
    @JsonProperty
    private LocalDate date;

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

    Visit() {
        super();
        this.date = LocalDate.of(2000, 1, 1);
        this.text = "";
        this.pet = null;
        this.vet = null;
    }

    Visit(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.date = LocalDate.of(2000, 1, 1);
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
        return this.date.equals(that.date) &&
                this.text.equals(that.text) &&
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
        final Visit value = new Visit(getVersion(), id);
        value.date = this.date;
        value.text = this.text;
        value.pet = this.pet;
        value.vet = this.vet;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
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
        return new JsonMapper().writeJson(this);
    }

    public static Visit parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Visit.class);
    }
}
