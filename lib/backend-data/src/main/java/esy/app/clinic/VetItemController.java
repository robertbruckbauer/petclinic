package esy.app.clinic;

import com.querydsl.core.types.Predicate;
import esy.EsyBackendAware;
import esy.api.clinic.QVet;
import esy.api.clinic.Vet;
import esy.api.clinic.VetItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping(EsyBackendAware.API_PATH)
@RequiredArgsConstructor
public class VetItemController {

    private final VetRepository vetRepository;

    @GetMapping("/vet/search/findAllItem")
    public ResponseEntity<CollectionModel<VetItem>> findAllItem(@QuerydslPredicate(root = Vet.class) Predicate predicate) {
        final var allItem = new ArrayList<VetItem>();
        final var orderBy = QVet.vet.name.asc();
        for (final var value : vetRepository.findAll(predicate, orderBy)) {
            allItem.add(VetItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
