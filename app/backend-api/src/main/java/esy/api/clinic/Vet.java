package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "vet", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"name"})
})
public final class Vet extends JsonJpaEntity<Vet> {

    @Column(name = "name")
    @Getter
    @JsonProperty
    private String name;

    @ElementCollection(
            fetch = FetchType.EAGER)
    @CollectionTable(
            name = "vet_skill",
            joinColumns = @JoinColumn(name = "id"))
    @Column(name = "skill")
    @OrderBy
    @Getter
    @JsonProperty
    private SortedSet<String> allSkill;

    Vet() {
        super();
        this.name = "";
        this.allSkill = new TreeSet<>();
    }

    Vet(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.name = "";
        this.allSkill = new TreeSet<>();
    }

    @Override
    public String toString() {
        return super.toString() + ",name='" + name + "'";
    }

    @Override
    public boolean isEqual(final Vet that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return this.name.equals(that.name)&&
                this.allSkill.equals(that.allSkill);
    }

    @Override
    public Vet verify() {
        // Check if name is valid
        if (name.isBlank()) {
            throw new IllegalArgumentException("name is blank");
        }
        return this;
    }

    @Override
    public Vet withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final Vet value = new Vet(getVersion(), id);
        value.name = this.name;
        value.allSkill = this.allSkill;
        return value;
    }

    @JsonAnyGetter
    private Map<String, Object> extraJson() {
        final Map<String, Object> allExtra = new HashMap<>();
        allExtra.put("version", getVersion());
        return allExtra;
    }

    @JsonIgnore
    public Vet addAllSkill(@NonNull final String... text) {
        this.allSkill.addAll(List.of(text));
        return this;
    }

    @JsonIgnore
    public Vet addAllSkill(@NonNull final String text, @NonNull final String regex) {
        this.allSkill.addAll(List.of(text.split(regex)));
        return this;
    }

    @Override
    public String writeJson() {
        return new JsonMapper().writeJson(this);
    }

    public static Vet parseJson(@NonNull final String json) {
        return new JsonMapper().parseJson(json, Vet.class);
    }
}
