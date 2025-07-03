package esy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import esy.EsyBackendAware;
import esy.EsyBackendEntity;
import esy.EsyBackendRoot;
import esy.EsyEntityRoot;
import esy.auth.Cors;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;


import static org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;

/**
 * @see <a href="https://spring.io/projects/spring-boot">Spring Boot project</a>
 * @see <a href="https://spring.io/projects/spring-security">Spring Security project</a>
 * @see <a href="https://www.baeldung.com/security-spring">Spring Security tutorials</a>
 * @see <a href="https://spring.io/projects/spring-data-jpa">Spring Data JPA project</a>
 * @see <a href="https://spring.io/projects/spring-data-rest">Spring Data REST project</a>
 * @see <a href="https://www.baeldung.com/spring-open-session-in-view">A Guide to Spring’s Opn Session in View</a>
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.java-config">Annotation-based Configuration</a>>
 * @see <a href="https://stackoverflow.com/questions/44295231/how-to-set-default-value-of-exported-as-false-in-rest-resource-spring-data-rest">How to set default value of exported</a>
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.java-config">Annotation-based Configuration</a>>
 * @see <a href="https://docs.spring.io/spring-data/rest/docs/current/reference/html/#getting-started.basic-settings">Basic Settings for Spring Data REST</a>
 * @see <a href="https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa">A Guide to JPA with Spring</a>
 * @see <a href="https://thorben-janssen.com/tutorials">Tutorials von Thorben Jansen</a>
 * @see <a href="https://vladmihalcea.com/tutorials">Tutorials von Vlad Mihalcea</a> */
@Configuration
@ComponentScan(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
@EntityScan(
        basePackages = {EsyEntityRoot.PACKAGE_NAME},
        basePackageClasses = {EsyBackendEntity.class})
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:database.properties")
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:endpoint.properties")
@EnableJpaRepositories(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
@EnableTransactionManagement
@EnableScheduling
@RequiredArgsConstructor
public class EsyBackendConfiguration implements EsyBackendAware {

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

    @Bean
    public WebSecurityCustomizer securityCustomizer() {
        return web -> {
            // do not handle static resources
            web.ignoring().requestMatchers("/static/**");
            // do not handle version endpoints
            web.ignoring().requestMatchers(VERSION_PATH, VERSION_PATH + ".*");
            // do not handle health endpoints
            web.ignoring().requestMatchers(EndpointRequest.to(HealthEndpoint.class));
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(customizer -> customizer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull final CorsRegistry registry) {
                applyCorsConfiguration(registry);
            }
        };
    }

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurer() {

            @Override
            public void configureRepositoryRestConfiguration(@NonNull final RepositoryRestConfiguration configuration, final CorsRegistry registry) {
                // apply defaults
                configuration.setBasePath(API_PATH);
                configuration.setRepositoryDetectionStrategy(RepositoryDetectionStrategies.ANNOTATED);
                // create JSON with content (not _embedded)
                configuration.setDefaultMediaType(MediaType.APPLICATION_JSON);
                configuration.useHalAsDefaultJsonMediaType(false);
                // expose id for value objects
                applyJsonConfiguration(configuration);
                // apply CORS settings
                applyCorsConfiguration(registry);
            }

            @Override
            public void configureJacksonObjectMapper(@NonNull final ObjectMapper mapper) {
                // apply defaults
                JsonMapper.configure(mapper);
            }
        };
    }
}
