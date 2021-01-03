package esy.app;

import esy.http.RestApiConnection;
import esy.http.RestApiResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class BackendHsqlRunnerTest {

	@LocalServerPort
	private int port;

	String toBackendUrl(final String path) {
		return "http://localhost:" + port + path;
	}

	@Test
	@Order(1)
	void getHealth() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}"));
	}

	@Test
	@Order(2)
	void getLiveness() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health/liveness")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\"}"));
	}

	@Test
	@Order(3)
	void getReadiness() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health/readiness")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\"}"));
	}

	@Test
	@Order(4)
	void getMappings() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/mappings")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(5)
	void getMetrics() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/metrics")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(6)
	void getLiquibase() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/liquibase")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
	}

	@Test
	@Order(10)
	void getVersion() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/version")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Map<?,?> allValue = result.toObject(Map.class);
		assertEquals(2, allValue.size());
		assertTrue(allValue.containsKey("major"));
		assertTrue(allValue.containsKey("minor"));
	}
}
