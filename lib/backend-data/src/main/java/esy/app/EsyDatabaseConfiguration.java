package esy.app;

import esy.EsyBackendAware;
import esy.EsyBackendEntity;
import esy.EsyBackendRoot;
import esy.EsyEntityRoot;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @see <a href="https://spring.io/projects/spring-data-jpa">Spring Data JPA project</a>
 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.java-config">Annotation-based Configuration</a>>
 * @see <a href="https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa">A Guide to JPA with Spring</a>
 * @see <a href="https://thorben-janssen.com/tutorials">Tutorials von Thorben Jansen</a>
 * @see <a href="https://vladmihalcea.com/tutorials">Tutorials von Vlad Mihalcea</a>
 **/
// tag::configuration[]
@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:database.properties")
@EntityScan(
        basePackages = {EsyEntityRoot.PACKAGE_NAME},
        basePackageClasses = {EsyBackendEntity.class})
@ComponentScan(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
@EnableJpaRepositories(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
@EnableTransactionManagement
// end::configuration[]
public class EsyDatabaseConfiguration implements EsyBackendAware {
}
