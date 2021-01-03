package esy.app.clinic;

import esy.api.clinic.Vet;
import esy.api.clinic.VetItem;
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
import java.util.List;

@RepositoryEventHandler
@BasePathAwareController
public class VetRestController extends JsonJpaRestControllerBase<Vet> {

    private final VetRepository vetRepository;

    @Autowired
    public VetRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate,
            @NonNull final VetRepository vetRepository) {
        super(eventPublisher, transactionTemplate);
        this.vetRepository = vetRepository;
    }

    @GetMapping("/vet/search/findAllItem")
    public ResponseEntity<CollectionModel<VetItem>> findAllItem() {
        final List<VetItem> allItem = new ArrayList<>();
        for (final Vet value : vetRepository.findAllByOrderByNameAsc()) {
            allItem.add(VetItem.fromValue(value));
        }
        return ResponseEntity.status(HttpStatus.OK).body(CollectionModel.of(allItem));
    }
}
