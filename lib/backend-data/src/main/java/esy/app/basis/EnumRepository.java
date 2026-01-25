package esy.app.basis;

import esy.api.basis.Enum;
import esy.api.basis.QEnum;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface EnumRepository extends JsonJpaRepository<Enum>, QuerydslRepository<Enum, QEnum> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QEnum root) {
        bindings.bind(root.code).all(this::longEqBetweenInBinding);
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
    }

    /**
     * Returns all persisted values of given discriminator.
     *
     * @param art discriminator
     * @return persisted values
     */
    @Query("SELECT e FROM Enum e " +
            "WHERE e.art = ?1 " +
            "ORDER BY e.art ASC, e.code")
    List<Enum> findAll(String art);

    /**
     * Returns all persisted values of given discriminator
     * for a given unique code.
     * Orders by {@code name} column.
     *
     * @param art discriminator
     * @param code unique code
     * @return persisted values
     */
    @Query("SELECT e FROM Enum e " +
            "WHERE e.art = ?1 " +
            "AND e.code = ?2 " +
            "ORDER BY e.art ASC, e.code")
    Optional<Enum> findByCode(String art, Long code);

    /**
     * Returns all persisted values of given discriminator
     * for a given unique name.
     * Orders by {@code name} column.
     *
     * @param art discriminator
     * @param name unique name
     * @return persisted values
     */
    @Query("SELECT e FROM Enum e " +
            "WHERE e.art = ?1 " +
            "AND e.name = ?2 " +
            "ORDER BY e.art ASC, e.code")
    Optional<Enum> findByName(String art, String name);

    /**
     * Returns the number of persisted values of given discriminator.
     *
     * @param art discriminator
     * @return the number of values
     */
    @Query("SELECT COUNT(e) FROM Enum e " +
            "WHERE e.art = ?1")
    long count(String art);
}
