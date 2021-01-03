package esy.app.clinic;

import esy.api.clinic.Visit;
import esy.rest.JsonJpaRestControllerBase;
import io.micrometer.core.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.transaction.support.TransactionTemplate;

@RepositoryEventHandler
@BasePathAwareController
public class VisitRestController extends JsonJpaRestControllerBase<Visit> {

    @Autowired
    public VisitRestController(
            @NonNull final ApplicationEventPublisher eventPublisher,
            @NonNull final TransactionTemplate transactionTemplate) {
        super(eventPublisher, transactionTemplate);
    }
}
