package esy.app;

import esy.graphql.LocalDateScalar;
import esy.graphql.LocalTimeScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:graphql.properties")
public class EsyGraphqlConfiguration {

    // tag::graphqlWiringConfigurer[]
    @Bean
    public RuntimeWiringConfigurer graphqlWiringConfigurer() {
        return builder -> builder
                .scalar(LocalDateScalar.createScalarType())
                .scalar(LocalTimeScalar.createScalarType());
    }
    // end::graphqlWiringConfigurer[]
}
