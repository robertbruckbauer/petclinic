package esy.app;

import lombok.NonNull;
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
 *
 * @see <a href="https://github.com/spring-projects/spring-data-rest/issues/1709">
 *     Empty collections are not serialized properly with default media type json</a>
 * @see <a href="https://github.com/spring-projects/spring-hateoas-examples/blob/main/spring-hateoas-and-spring-data-rest/README.adoc">
 *     Spring HATEOAS combined with Spring Data REST</a>
 */
@Component
public class CollectionModelProcessor implements RepresentationModelProcessor<CollectionModel<Object>> {

    @NonNull
    @Override
    public CollectionModel<Object> process(@NonNull final CollectionModel<Object> model) {
        if (model.getContent().size() != 1) {
            return model;
        }
        if (!model.getContent().iterator().next().getClass().getCanonicalName().contains("EmptyCollectionEmbeddedWrapper")) {
            return model;
        }
        return CollectionModel.of(List.of()).add(model.getLinks());
    }
}
