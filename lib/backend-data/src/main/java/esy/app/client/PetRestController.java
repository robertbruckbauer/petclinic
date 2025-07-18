package esy.app.client;

import esy.api.client.Pet;
import esy.api.client.PetItem;
import esy.api.client.QPet;
import esy.rest.JsonJpaRestControllerBase;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@RepositoryEventHandler
@BasePathAwareController
public class PetRestController extends JsonJpaRestControllerBase<Pet> {

    private final PetRepository petRepository;

    @Autowired
    public PetRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final PetRepository petRepository) {
        super(eventPublisher, transactionTemplate);
        this.petRepository = petRepository;
    }

    @GetMapping("/pet/search/findAllItem")
    public ResponseEntity<CollectionModel<PetItem>> findAllItem() {
        final var allItem = new ArrayList<PetItem>();
        final var orderBy = QPet.pet.name.asc();
        for (final var value : petRepository.findAll(orderBy)) {
            allItem.add(PetItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
