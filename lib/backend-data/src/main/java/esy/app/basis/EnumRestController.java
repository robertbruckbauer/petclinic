package esy.app.basis;

import esy.api.basis.Enum;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@BasePathAwareController
@RequiredArgsConstructor
public class EnumRestController {

    private final EnumRepository enumRepository;

    @GetMapping("/enum/{art}")
    public ResponseEntity<CollectionModel<Enum>> findAll(@PathVariable final String art) {
        final var allEnum = enumRepository.findAll(art);
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allEnum));
    }

    @PostMapping("/enum/{art}")
    public ResponseEntity<Enum> createEnum(@PathVariable final String art, @RequestBody final Enum body) {
        final var value = enumRepository.save(body.setArt(art).verify());
        return ResponseEntity.status(HttpStatus.CREATED).body(value);
    }

    @PutMapping("/enum/{art}/{code}")
    public ResponseEntity<Enum> updateEnum(@PathVariable final String art, @PathVariable final Long code, @RequestBody final Enum body) {
        final var value = enumRepository.save(
                enumRepository.findByCode(art, code)
                        .orElseThrow(() ->
                                new DataRetrievalFailureException("Enum(" + art + ", " + code + ") not found"))
                        .setName(body.getName())
                        .setText(body.getText()));
        return ResponseEntity.status(HttpStatus.OK).body(value);
    }

    @DeleteMapping("/enum/{art}/{code}")
    public ResponseEntity<Enum> deleteEnum(@PathVariable("art") final String art, @PathVariable("code") final Long code) {
        final var value = enumRepository.findByCode(art, code)
                .orElseThrow(() ->
                        new DataRetrievalFailureException("Enum(" + art + ", " + code + ") not found"));
        enumRepository.delete(value);
        return ResponseEntity.status(HttpStatus.OK).body(value);
    }
}
