package esy.app.basis;

import esy.api.basis.Ping;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@BasePathAwareController
@RequiredArgsConstructor
public class PingRestController {

    private final PingRepository pingRepository;

    @GetMapping("/ping")
    public ResponseEntity<CollectionModel<Ping>> findAll() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @PostMapping("/ping")
    public ResponseEntity<EntityModel<Ping>> createPing() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @GetMapping("/ping/{id}")
    public ResponseEntity<EntityModel<Ping>> findById(@PathVariable final UUID id) {
        final var value = pingRepository.findById(id).orElseThrow(() ->
                new DataRetrievalFailureException("Ping(" + id + ") not found"));
        return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(value));
    }

    @PutMapping("/ping/{id}")
    public ResponseEntity<EntityModel<Ping>> touchPing(@PathVariable final UUID id) {
        final var value = pingRepository.findById(id).orElse(Ping.fromId(id));
        final var status = value.isPersisted() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(EntityModel.of(pingRepository.save(value.touch().verify())));
    }

    @DeleteMapping("/ping/{id}")
    public ResponseEntity<EntityModel<Ping>> deletePing(@PathVariable final UUID id) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
