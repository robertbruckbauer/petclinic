package esy.app;

import esy.graphql.LocalDateScalar;
import org.springframework.context.annotation.*;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphqlConfiguration {

    @Bean
    public RuntimeWiringConfigurer graphqlWiringConfigurer() {
        return builder -> builder
                .scalar(LocalDateScalar.createLocalDateScalar());
    }
}
