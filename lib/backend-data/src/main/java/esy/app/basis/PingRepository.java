package esy.app.basis;

import esy.api.basis.Ping;
import esy.api.basis.QPing;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface PingRepository extends JsonJpaRepository<Ping>, QuerydslRepository<Ping, QPing> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QPing root) {
        bindings.bind(root.at).all(this::localDateTimeEqBetweenInBinding);
    }
}
