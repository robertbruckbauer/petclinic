package esy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.NonNull;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;

@Configuration
@ComponentScan(basePackages = {"esy.app"})
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:endpoint.properties")
public class EndpointConfiguration {

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
        registry.addMapping("/**")
                .allowCredentials(CORS.getAllowCredentials())
                .allowedHeaders(CORS.getAllowedHeaders().toArray(String[]::new))
                .allowedMethods(CORS.getAllowedMethods().toArray(String[]::new))
                .allowedOriginPatterns(CORS.getAllowedOriginPatterns().toArray(String[]::new))
                .maxAge(CORS.getMaxAge());
    }

    void applyJsonConfiguration(@NonNull final String packageName, @NonNull final RepositoryRestConfiguration configuration) {
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(JsonJpaEntity.class));
        provider.findCandidateComponents(packageName.replace(".", "/")).stream()
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

    /**
     * {@see <a href="https://spring.io/projects/spring-data-rest" />}
     * {@see <a href="https://stackoverflow.com/questions/44295231/how-to-set-default-value-of-exported-as-false-in-rest-resource-spring-data-rest" />}
     */
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
                applyJsonConfiguration("esy.api", configuration);
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
