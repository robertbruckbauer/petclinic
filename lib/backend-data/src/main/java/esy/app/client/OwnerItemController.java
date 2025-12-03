package esy.app.client;

import com.querydsl.core.types.Predicate;
import esy.EsyBackendAware;
import esy.api.client.Owner;
import esy.api.client.OwnerItem;
import esy.api.client.QOwner;
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
public class OwnerItemController {

    private final OwnerRepository ownerRepository;

    @GetMapping("/owner/search/findAllItem")
    public ResponseEntity<CollectionModel<OwnerItem>> findAllItem(@QuerydslPredicate(root = Owner.class) Predicate predicate) {
        final var allItem = new ArrayList<OwnerItem>();
        final var orderBy = QOwner.owner.name.asc();
        for (final var value : ownerRepository.findAll(predicate, orderBy)) {
            allItem.add(OwnerItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
