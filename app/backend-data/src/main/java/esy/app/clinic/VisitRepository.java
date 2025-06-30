package esy.app.clinic;

import esy.api.clinic.QVisit;
import esy.api.clinic.Visit;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "visit", collectionResourceRel = "allVisit")
public interface VisitRepository extends JsonJpaRepository<Visit>, QuerydslRepository<Visit, QVisit> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QVisit root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
        bindings.bind(root.date).all(this::localDateEqBetweenInBinding);
    }
}
