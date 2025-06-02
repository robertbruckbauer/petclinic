package esy.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import esy.json.JsonJpaEntity;
import esy.json.JsonMapper;
import lombok.NonNull;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy.RepositoryDetectionStrategies;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
@EnableWebSecurity
public class BackendConfiguration {

    /**
     * CORS configuration for all REST API requests.
     * Same values must apply to Spring Security,
     * Spring Web AND Spring Data.
     */
    void applyCorsConfiguration(@NonNull final CorsRegistry registry) {
        final var registration = registry.addMapping("/**");
        registration.allowedHeaders("*");
        registration.allowedMethods("*");
        registration.allowedOriginPatterns(
                "https://*.app.github.dev",
                "http://localhost:*",
                "http://127.0.0.1:*"
        );
        registration.allowCredentials(true);
        registration.allowPrivateNetwork(true);
        registration.maxAge(3600L);
    }

    /**
     * JSON configuration for all JPA entities.
     */
    void applyJsonConfiguration(@NonNull final RepositoryRestConfiguration configuration) {
        final var provider = new ClassPathScanningCandidateComponentProvider(false);
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
    public SecurityFilterChain securityFilterChain(@NonNull final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
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
