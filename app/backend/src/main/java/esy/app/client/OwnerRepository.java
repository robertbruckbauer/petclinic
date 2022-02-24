package esy.app.client;

import esy.api.client.Owner;
import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "owner", collectionResourceRel = "allOwner")
public interface OwnerRepository extends JsonJpaRepository<Owner> {

    /**
     * Returns all persisted entities.
     * Orders by {@code name} column.
     *
     * @return persisted entities
     */
    List<Owner> findAllByOrderByNameAsc();

    /**
     * Returns a persisted entity with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted entity or nothing
     */
    Optional<Owner> findByName(@Param("name") String name);
}
