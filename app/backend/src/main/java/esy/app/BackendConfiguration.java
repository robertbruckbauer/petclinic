package esy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.NonNull;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;

@Configuration
@ComponentScan(basePackages = {"esy.app"})
@EntityScan(basePackages = {"esy.api"})
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:endpoint.properties")
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:database.properties")
@EnableTransactionManagement
public class BackendConfiguration {

    /**
     * CORS configuration for all REST API requests.
     * Same values must apply to Spring Security,
     * Spring Web AND Spring Data.
     */
    static final CorsConfiguration CORS = new CorsConfiguration();

    static {
        // tag::cors[]
        CORS.setAllowCredentials(true);
        CORS.addAllowedHeader(HttpHeaders.ACCEPT);
        CORS.addAllowedHeader(HttpHeaders.AUTHORIZATION);
        CORS.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
        CORS.addAllowedHeader(HttpHeaders.CONTENT_LENGTH);
        CORS.addAllowedMethod(HttpMethod.GET.name());
        CORS.addAllowedMethod(HttpMethod.POST.name());
        CORS.addAllowedMethod(HttpMethod.PUT.name());
        CORS.addAllowedMethod(HttpMethod.PATCH.name());
        CORS.addAllowedMethod(HttpMethod.DELETE.name());
        CORS.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://localhost",
                "https://localhost"));
        CORS.setMaxAge(3600L);
        // end::cors[]
    }

    void applyCorsConfiguration(@NonNull final CorsRegistry registry) {
        final var registration = registry.addMapping("/**");
        Optional.ofNullable(CORS.getAllowCredentials())
                .ifPresent(registration::allowCredentials);
        Optional.ofNullable(CORS.getAllowedHeaders())
                .ifPresent(e -> registration.allowedHeaders(e.toArray(String[]::new)));
        Optional.ofNullable(CORS.getAllowedMethods())
                .ifPresent(e -> registration.allowedMethods(e.toArray(String[]::new)));
        Optional.ofNullable(CORS.getAllowedOriginPatterns())
                .ifPresent(e -> registration.allowedOriginPatterns(e.toArray(String[]::new)));
        Optional.ofNullable(CORS.getMaxAge())
                .ifPresent(registration::maxAge);
    }

    void applyJsonConfiguration(@NonNull final RepositoryRestConfiguration configuration) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(JsonJpaEntity.class));
        provider.findCandidateComponents("esy/api").stream()
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
                configuration.setBasePath("/api");
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
