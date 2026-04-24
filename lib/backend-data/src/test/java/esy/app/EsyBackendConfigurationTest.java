package esy.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.autoconfigure.web.DataWebProperties;
import org.springframework.boot.data.rest.autoconfigure.DataRestProperties;
import org.springframework.boot.graphql.autoconfigure.GraphQlProperties;
import org.springframework.boot.health.autoconfigure.actuate.endpoint.HealthEndpointProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("slow")
@SpringBootTest
@EnableConfigurationProperties({
		DataWebProperties.class,
		DataRestProperties.class})
public class EsyBackendConfigurationTest {

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ServerProperties serverProperties;

	@Autowired
	private HealthEndpointProperties healthEndpointProperties;

	@Autowired
	private WebEndpointProperties webEndpointProperties;

	@Autowired
	private DataWebProperties dataWebProperties;

	@Autowired
	private DataRestProperties dataRestProperties;

	@Autowired
	private LiquibaseProperties liquibaseProperties;

	@Autowired
	private DataSourceProperties datasourceProperties;

	@Autowired
	private HibernateProperties hibernateProperties;

	@Autowired
	private JpaProperties jpaProperties;

	@Autowired
	private GraphQlProperties graphQlProperties;

	@Test
	void context() {
		assertNotNull(context);
		assertNotNull(publisher);
		assertNotNull(resourceLoader);
		assertNotNull(context.getBean(EsyEndpointConfiguration.class));
		assertNotNull(context.getBean(EsyDatabaseConfiguration.class));
		assertNotNull(context.getBean(EsyGraphqlConfiguration.class));
		assertNotNull(context.getBean(EsySecurityConfiguration.class));
		assertNotNull(context.getBean(CollectionModelProcessor.class));
	}

	@Test
	void endpointProperties() {
		assertNotNull(serverProperties);
		assertFalse(serverProperties.getCompression().getEnabled());
		assertNotNull(healthEndpointProperties);
		assertNotNull(webEndpointProperties);
		assertNotNull(dataWebProperties);
		assertEquals(9999, dataWebProperties.getPageable().getDefaultPageSize());
		assertEquals(9999, dataWebProperties.getPageable().getMaxPageSize());
		assertNotNull(dataRestProperties);
		assertEquals(9999, dataRestProperties.getDefaultPageSize());
		assertEquals(9999, dataRestProperties.getMaxPageSize());
	}

	@Test
	void databaseProperties() {
		assertNotNull(liquibaseProperties);
		assertTrue(liquibaseProperties.isEnabled());
		assertEquals("PUBLIC", liquibaseProperties.getDefaultSchema());
		assertEquals("PUBLIC", liquibaseProperties.getLiquibaseSchema());
		assertEquals("classpath:liquibase/changelog.xml", liquibaseProperties.getChangeLog());
		assertNotNull(datasourceProperties);
		assertEquals("jdbc:hsqldb:mem:db;DB_CLOSE_DELAY=-1", datasourceProperties.getUrl());
		assertEquals("SA", datasourceProperties.getUsername());
		assertEquals("", datasourceProperties.getPassword());
		assertNotNull(hibernateProperties);
		assertNull(hibernateProperties.getDdlAuto());
		assertNotNull(jpaProperties);
		assertEquals(Boolean.FALSE, jpaProperties.getOpenInView());
	}

	@Test
	void graphqlProperties() {
		assertNotNull(graphQlProperties);
		assertEquals("/api/graphql", graphQlProperties.getHttp().getPath());
		assertEquals("/api/graphiql", graphQlProperties.getGraphiql().getPath());
		assertTrue(graphQlProperties.getGraphiql().isEnabled());
	}

	@Test
	void corsRegsitry() {
		final var classUnderTest = new EsyEndpointConfiguration();
		final var corsRegistry = new CorsRegistry();
		assertDoesNotThrow(() -> classUnderTest.applyCorsConfiguration(corsRegistry));
	}

	@ParameterizedTest
	@CsvSource({
			"http://localhost,false",
			"http://localhost:5000,true"
	})
	void corsLocalhost(final String url, boolean allowed) {
		final var corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:*"));
		assertEquals(allowed, corsConfiguration.checkOrigin(url) != null);
	}

	@ParameterizedTest
	@CsvSource({
			"http://127.0.0.1,false",
			"http://127.0.0.1:5000,true"
	})
	void corsLoopback(final String url, boolean allowed) {
		final var corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(List.of("http://127.0.0.1:*"));
		assertEquals(allowed, corsConfiguration.checkOrigin(url) != null);
	}

	@ParameterizedTest
	@CsvSource({
			"https://fuzzy-enigma-97q96w9vjxp2xpxp,false",
			"https://fuzzy-enigma-97q96w9vjxp2xpxp-5000.app.github.dev,true"
	})
	void corsCodespace(final String url, boolean allowed) {
		final var corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowedOriginPatterns(List.of("https://*.app.github.dev"));
		assertEquals(allowed, corsConfiguration.checkOrigin(url) != null);
	}
}
