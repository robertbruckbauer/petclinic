package esy.app;

import esy.EsyBackendAware;
import esy.EsyBackendRoot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

/**
 * @see <a href="https://spring.io/projects/spring-boot">Spring Boot project</a>
 **/
// tag::configuration[]
@Configuration
@PropertySource(
        ignoreResourceNotFound = false,
        value = "classpath:schedule.properties")
@ComponentScan(
        basePackages = {EsyBackendRoot.PACKAGE_NAME})
@EnableScheduling
// end::configuration[]
public class EsyScheduleConfiguration implements EsyBackendAware {

    // tag::clock[]
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
    // end::clock[]
}
