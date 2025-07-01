package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.OwnerItem;
import esy.api.client.QOwner;
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
public class OwnerRestController extends JsonJpaRestControllerBase<Owner> {

    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final OwnerRepository ownerRepository) {
        super(eventPublisher, transactionTemplate);
        this.ownerRepository = ownerRepository;
    }

    @GetMapping("/owner/search/findAllItem")
    public ResponseEntity<CollectionModel<OwnerItem>> findAllItem() {
        final var allItem = new ArrayList<OwnerItem>();
        final var orderBy = QOwner.owner.name.asc();
        for (final var value : ownerRepository.findAll(orderBy)) {
            allItem.add(OwnerItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
