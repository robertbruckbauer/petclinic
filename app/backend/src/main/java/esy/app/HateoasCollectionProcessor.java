package esy.app;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Returns an empty resource collection if no real data
 * is present.
 * <p>
 * This happens because empty resource collections are
 * wrapped to an {@code EmptyCollectionEmbeddedWrapper}
 * no matter what media type is used.
 * <p>
 * {@see <a href="https://jira.spring.io/browse/DATAREST-1346"/>}
 */
@Component
public class HateoasCollectionProcessor implements RepresentationModelProcessor<CollectionModel<Object>> {

    @Override
    public CollectionModel<Object> process(final CollectionModel<Object> model) {
        if (model.getContent().size() != 1) {
            return model;
        }
        if (!model.getContent().iterator().next().getClass().getCanonicalName().contains("EmptyCollectionEmbeddedWrapper")) {
            return model;
        }
        return CollectionModel.of(List.of()).add(model.getLinks());
    }
}
