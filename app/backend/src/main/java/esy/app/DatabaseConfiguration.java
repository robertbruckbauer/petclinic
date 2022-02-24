package esy.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {"esy.app"})
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:database.properties")
@EntityScan(basePackages = {"esy.api"})
@EnableJpaRepositories(basePackages = {"esy.app"})
@EnableTransactionManagement
public class DatabaseConfiguration {
}
