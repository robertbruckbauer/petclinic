package esy.api.client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "owner", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name"})
})
public final class Owner extends JsonJpaEntity<Owner> {

    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    @Column(name = "address")
    @Getter
    @JsonProperty
    private String address;

    @Column(name = "contact")
    @Getter
    @JsonProperty
    private String contact;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Pet> allPet;

    Owner() {
        super();
        this.name = "";
        this.address = "";
        this.contact = "";
        this.allPet = new LinkedHashSet<>();
    }

    Owner(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
        this.address = "";
        this.contact = "";
        this.allPet = new LinkedHashSet<>();
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name + "'";
    }

    @Override
    public boolean isEqual(final Owner that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.name.equals(that.name) &&
                this.address.equals(that.address) &&
                this.contact.equals(that.contact) &&
                this.allPet.equals(that.allPet);
    }

    @Override
    public Owner verify() {
        // Check if name is valid
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        // Check if address is valid
        if (address.isBlank()) {
            throw new IllegalArgumentException("address is blank");
        }
        return this;
    }

    @Override
    public Owner withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Owner value = new Owner(getVersion(), id);
        value.name = this.name;
        value.address = this.address;
        value.contact = this.contact;
        value.allPet = this.allPet;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        allExtra.put("allPetItem", allPet.stream()
                .map(PetItem::fromValue)
                .collect(Collectors.toSet()));
        return allExtra;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Owner parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Owner.class);
    }
}
