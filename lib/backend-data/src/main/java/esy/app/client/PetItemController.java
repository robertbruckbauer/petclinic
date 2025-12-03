package esy.app.client;

import com.querydsl.core.types.Predicate;
import esy.EsyBackendAware;
import esy.api.client.Pet;
import esy.api.client.PetItem;
import esy.api.client.QPet;
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
public class PetItemController {

    private final PetRepository petRepository;

    @GetMapping("/pet/search/findAllItem")
    public ResponseEntity<CollectionModel<PetItem>> findAllItem(@QuerydslPredicate(root = Pet.class) Predicate predicate) {
        final var allItem = new ArrayList<PetItem>();
        final var orderBy = QPet.pet.name.asc();
        for (final var value : petRepository.findAll(predicate, orderBy)) {
            allItem.add(PetItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
