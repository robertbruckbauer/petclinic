package esy.app.clinic;

import esy.api.clinic.Vet;
import esy.rest.JsonJpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "vet", collectionResourceRel = "allVet")
public interface VetRepository extends JsonJpaRepository<Vet> {

    /**
     * Returns all persisted entities.
     * Orders by {@code name} column.
     *
     * @return persisted entities
     */
    List<Vet> findAllByOrderByNameAsc();

    /**
     * Returns a persisted entity with given unique name
     * or nothing, if no value exists.
     *
     * @param name unique name
     * @return persisted entity or nothing
     */
    Optional<Vet> findByName(@Param("name") String name);
}
