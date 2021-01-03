package esy.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:database.properties")
@EntityScan(basePackages = "esy.api")
@EnableJpaRepositories
@EnableTransactionManagement
public class DatabaseConfiguration {
}
