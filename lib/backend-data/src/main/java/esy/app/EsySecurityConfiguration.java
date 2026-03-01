package esy.app;

import esy.EsyBackendAware;
import esy.EsyBackendRoot;
import lombok.NonNull;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @see <a href="https://spring.io/projects/spring-boot">Spring Boot project</a>
 * @see <a href="https://spring.io/projects/spring-security">Spring Security project</a>
 * @see <a href="https://www.baeldung.com/security-spring">Spring Security tutorials</a>
 **/
// tag::configuration[]
@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:security.properties")
@ComponentScan(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
// end::configuration[]
public class EsySecurityConfiguration implements EsyBackendAware {

    // tag::securityCustomizer[]
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
    // end::securityCustomizer[]

    // tag::securityFilterChain[]
    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull final HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(customizer -> customizer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
    // end::securityFilterChain[]
}
