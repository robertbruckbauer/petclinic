package esy.app.basis;

import esy.api.basis.Ping;
import esy.rest.JsonJpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface PingRepository extends JsonJpaRepository<Ping> {
}
