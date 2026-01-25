package esy.app;

import esy.EsyBackendAware;
import esy.EsyBackendRoot;
import esy.EsyEntityRoot;
import esy.auth.Cors;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;

/**
 * @see <a href="https://spring.io/projects/spring-boot">Spring Boot project</a>
 * @see <a href="https://spring.io/projects/spring-data-rest">Spring Data REST project</a>
 * @see <a href="https://www.baeldung.com/spring-open-session-in-view">A Guide to Spring’s Opn Session in View</a>
 * @see <a href="https://stackoverflow.com/questions/44295231/how-to-set-default-value-of-exported-as-false-in-rest-resource-spring-data-rest">How to set default value of exported</a>
 * @see <a href="https://docs.spring.io/spring-data/rest/docs/current/reference/html/#getting-started.basic-settings">Basic Settings for Spring Data REST</a>
 **/
// tag::configuration[]
@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:endpoint.properties")
@ComponentScan(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
// end::configuration[]
public class EsyEndpointConfiguration implements EsyBackendAware {

    /**
     * CORS configuration for all REST API requests.
     * Same values must apply to Spring Security,
     * Spring Web AND Spring Data.
     */
    void applyCorsConfiguration(@NonNull final CorsRegistry registry) {
        final var registration = registry.addMapping("/**");
        Cors.withCredentials(registration::allowCredentials);
        Cors.withHeader(registration::allowedHeaders);
        Cors.withMethod(registration::allowedMethods);
        Cors.withLocalhost(registration::allowedOriginPatterns);
        Cors.withMaxAge(registration::maxAge);
        Cors.withPrivateNetwork(registration::allowPrivateNetwork);
    }

    /**
     * JSON configuration for all JPA entities.
     */
    void applyJsonConfiguration(@NonNull final RepositoryRestConfiguration configuration) {
        final var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(JsonJpaEntity.class));
        // show ID in JSON payload
        provider.findCandidateComponents(EsyEntityRoot.PACKAGE_PATH)
                .stream()
                .map(bean -> {
                    try {
                        return Class.forName(bean.getBeanClassName());
                    } catch (final ClassNotFoundException e) {
                        return JsonJpaEntity.class;
                    }
                })
                .forEach(configuration::exposeIdsFor);
    }

    // tag::jacksonCustomizer[]
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.postConfigurer(JsonJpaMapper::configure);
    }
    // end::jacksonCustomizer[]

    // tag::webMvcConfigurer[]
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull final CorsRegistry registry) {
                applyCorsConfiguration(registry);
            }
        };
    }
    // end::webMvcConfigurer[]

    // tag::repositoryRestConfigurer[]
    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {

            @Override
            public void configureRepositoryRestConfiguration(@NonNull final RepositoryRestConfiguration configuration, final CorsRegistry registry) {
                // apply REST defaults
                configuration.setBasePath(API_PATH);
                configuration.setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED);
                // create JSON with content (not _embedded)
                configuration.setDefaultMediaType(MediaType.APPLICATION_JSON);
                configuration.useHalAsDefaultJsonMediaType(false);
                configuration.setReturnBodyOnCreate(true);
                configuration.setReturnBodyOnUpdate(true);
                configuration.setReturnBodyOnDelete(true);
                // expose id for value objects
                applyJsonConfiguration(configuration);
                // apply CORS settings
                applyCorsConfiguration(registry);
            }
        };
    }
    // end::repositoryRestConfigurer[]
}
