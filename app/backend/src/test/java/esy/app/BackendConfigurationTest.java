package esy.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BackendConfigurationTest {

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ResourceLoader resourceLoader;

	@Test
	void context() {
		assertNotNull(context);
		assertNotNull(publisher);
		assertNotNull(resourceLoader);
		assertNotNull(context.getBean(BackendConfiguration.class));
		assertNotNull(context.getBean(CollectionModelProcessor.class));
	}

	private <T> void assertBeanExists(final T bean) {
		assertNotNull(bean, bean.toString());
		ReflectionUtils.doWithFields(bean.getClass(), field -> {
			if (!Modifier.isStatic(field.getModifiers())) {
				ReflectionUtils.makeAccessible(field);
				assertNotNull(ReflectionUtils.getField(field, bean), field.toString());
			}
		});
	}

	@Test
	void corsRegsitry() {
		final var classUnderTest = new BackendConfiguration();
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
