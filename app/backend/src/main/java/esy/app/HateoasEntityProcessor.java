package esy.app;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;

public class HateoasEntityProcessor implements RepresentationModelProcessor<EntityModel<Object>> {

    @Override
    public EntityModel<Object> process(final EntityModel<Object> model) {
        return model;
    }
}
