package esy.app.client;

import esy.api.client.Pet;
import esy.rest.JsonJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(path = "pet", collectionResourceRel = "allPet")
public interface PetRepository extends JsonJpaRepository<Pet> {

    /**
     * Returns all persisted entities.
     * Orders by {@code name} column.
     *
     * @return persisted entities
     */
    List<Pet> findAllByOrderByNameAsc();

    /**
     * Returns persisted entities for a {@link Owner}.
     * Orders by {@code name} column.
     *
     * @param ownerId a {@link Owner} id
     * @return persisted entities
     */
    @Query("SELECT p FROM Pet p " +
            "WHERE p.owner.id = ?1 " +
            "ORDER BY p.name ASC")
    List<Pet> findAllByOwner(UUID ownerId);

    /**
     * Returns a persisted entity with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted entity or nothing
     */
    Optional<Pet> findByName(@Param("name") String name);
}
