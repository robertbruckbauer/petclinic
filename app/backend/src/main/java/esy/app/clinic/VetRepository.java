package esy.app.clinic;

import esy.api.clinic.QVet;
import esy.api.clinic.Vet;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "vet", collectionResourceRel = "allVet")
public interface VetRepository extends JsonJpaRepository<Vet>, QuerydslRepository<Vet, QVet> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QVet root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
    }
}
