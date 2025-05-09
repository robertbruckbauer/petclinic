package esy.app.client;

import esy.api.client.Pet;
import esy.api.client.QPet;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "pet", collectionResourceRel = "allPet")
public interface PetRepository extends JsonJpaRepository<Pet>, QuerydslRepository<Pet, QPet> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QPet root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
    }
}
