package esy;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Die Klasse definiert die <i>base package</i> der standardisierten
 * Spring-Boot-Komponenten der Anwendung.
 * Die Klasse loggt Informationen über Phasen der Initialisierung
 * der Spring-Boot-Anwendung.
 */
@Component
@Slf4j
public final class EsyBackendRoot implements InitializingBean, DisposableBean, CommandLineRunner {

    public static final String PACKAGE_NAME = "esy.app";

    @Autowired
    private Environment env;

    public EsyBackendRoot() {
        if (!PACKAGE_NAME.startsWith(getClass().getPackageName())) {
            throw new IllegalStateException("expected " + PACKAGE_NAME);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("CREATED [{}].", getClass().getName());
    }

    @Override
    public void destroy() throws Exception {
        log.info("DELETED [{}].", getClass().getName());
    }

    @Override
    public void run(final String... args) throws Exception {
        log.info("STARTED [{}].", getClass().getName());
        for (final String arg: args) {
            log.info(arg);
        }
        // https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html
        logProperty("server.port");
        logProperty("spring.application.name");
        logProperty("spring.profiles.active");
        logProperty("spring.jpa.open-in-view");
        logProperty("spring.datasource.name");
        logProperty("spring.datasource.url");
        logProperty("spring.datasource.username");
        hasProperty("spring.datasource.password");
        logProperty("spring.jpa.properties.hibernate.show_sql");
        logProperty("spring.jpa.properties.hibernate.format_sql");
        logProperty("spring.jpa.properties.hibernate.generate_statistics");
        logProperty("management.endpoints.web.exposure.include");
        logProperty("management.endpoint.health.probes.enabled");
     }

    private void logProperty(@NonNull final String key) {
        final String value = env.getProperty(key);
        log.info("{}=[{}].", key, value);
    }

    private void hasProperty(@NonNull final String key) {
        final String value = env.getProperty(key);
        log.info("{}=[{}].", key, value != null && !value.isBlank());
    }
}
