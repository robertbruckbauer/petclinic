package esy;

import com.microsoft.playwright.Playwright;
import esy.rest.JsonJpaMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ServerRunnerTest {

	@Value(value = "${local.server.port}")
	private int port;

	String toBackendUrl(final String path) {
		return "http://localhost:" + port + path;
	}

	Playwright playwright() {
		final var options = new Playwright.CreateOptions()
				// https://playwright.dev/java/docs/browsers#skip-browser-downloads
				.setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD","1"));
		return Playwright.create(options);
	}

	@Test
	void healthApi() {
		try (final var playwright = playwright()) {
			final var req = playwright.request().newContext();
			final var res = req.get(toBackendUrl("/actuator/health"));
			assertThat(res.status(), equalTo(HttpStatus.OK.value()));
			final var rootNode = new JsonJpaMapper().parseJsonNode(res.text());
			assertEquals("UP",
					rootNode.path("status").asString());
			assertEquals("liveness",
					rootNode.path("groups").get(0).asString());
			assertEquals("readiness",
					rootNode.path("groups").get(1).asString());
		}
	}

	@Test
	void versionApi() {
		try (final var playwright = playwright()) {
			final var req = playwright.request().newContext();
			final var res = req.get(toBackendUrl("/version"));
			assertThat(res.status(), equalTo(HttpStatus.OK.value()));
			final var rootNode = new JsonJpaMapper().parseJsonNode(res.text());
			assertTrue(rootNode.hasNonNull("major"));
			assertTrue(rootNode.hasNonNull("minor"));
			assertTrue(rootNode.hasNonNull("version"));
		}
	}

	@Test
	void restApi() {
		try (final var playwright = playwright()) {
			final var assertion = new ServerApiAssertion(playwright, toBackendUrl(""));
			assertion.assertEnumSkill();
			assertion.assertEnumSpecies();
			assertion.assertOwner();
			assertion.assertPet();
			assertion.assertVet();
			assertion.assertVisit();
		}
	}
}
