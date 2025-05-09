package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.QOwner;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "owner", collectionResourceRel = "allOwner")
public interface OwnerRepository extends JsonJpaRepository<Owner>, QuerydslRepository<Owner, QOwner> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QOwner root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
    }
}
