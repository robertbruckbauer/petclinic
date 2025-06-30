package esy.app.client;

import esy.api.client.Pet;
import esy.api.client.QPet;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RepositoryRestResource(path = "pet", collectionResourceRel = "allPet")
public interface PetRepository extends JsonJpaRepository<Pet>, QuerydslRepository<Pet, QPet> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QPet root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
    }

    /**
     * Resolve the graphql query for {@link Pet} entities assigned
     * to one of the referenced owners.
     * Avoids the n+1 problem.
     *
     * @param allOwnerId referenced owners
     * @return resolved entities
     */
    List<Pet> findAllByOwnerIdIn(@NonNull Set<UUID> allOwnerId);
}
