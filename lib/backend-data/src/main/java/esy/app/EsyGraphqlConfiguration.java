package esy.app;

import esy.graphql.LocalDateScalar;
import esy.graphql.LocalTimeScalar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class EsyGraphqlConfiguration {

    @Bean
    public RuntimeWiringConfigurer graphqlWiringConfigurer() {
        return builder -> builder
                .scalar(LocalDateScalar.createScalarType())
                .scalar(LocalTimeScalar.createScalarType());
    }
}
