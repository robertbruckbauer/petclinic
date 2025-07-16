package esy;

import com.microsoft.playwright.Playwright;
import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import esy.http.RestApiConnection;
import esy.json.JsonMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.net.URLEncoder;
import java.time.Month;

import static java.nio.charset.StandardCharsets.UTF_8;
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

	@Test
	void healthApi() {
		try (final var playwright = Playwright.create()) {
			final var req = playwright.request().newContext();
			final var res = req.get(toBackendUrl("/actuator/health"));
			assertThat(res.status(), equalTo(HttpStatus.OK.value()));
			final var jsonReader = new JsonMapper().parseJsonPath(res.text());
			assertEquals("UP", jsonReader.read("$.status"));
			assertEquals("liveness", jsonReader.read("$.groups[0]"));
			assertEquals("readiness", jsonReader.read("$.groups[1]"));
		}
	}

	@Test
	void versionApi() {
		try (final var playwright = Playwright.create()) {
			final var req = playwright.request().newContext();
			final var res = req.get(toBackendUrl("/version"));
			assertThat(res.status(), equalTo(HttpStatus.OK.value()));
			final var jsonReader = new JsonMapper().parseJsonPath(res.text());
			assertNotNull(jsonReader.read("$.major"));
			assertNotNull(jsonReader.read("$.minor"));
			assertNotNull(jsonReader.read("$.version"));
		}
	}

	@Test
	void restApi() {
		try (final var playwright = Playwright.create()) {
			final var assertion = new PlaywrightApiAssertion(playwright, toBackendUrl(""));
			assertion.assertEnumSkill();
			assertion.assertEnumSpecies();
			assertion.assertOwner();
			assertion.assertPet();
		}
	}

	@Test
	@Order(30)
	void apiVet() throws Exception {
		final var name = "Mustermann";

		final var result1a = RestApiConnection.with(
						toBackendUrl("/api/vet"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final var value1 = result1a.toObject(Vet.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals("Alf " + name, value1.getName());

		final var result1b = RestApiConnection.with(
						toBackendUrl("/api/vet"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"" +
						"}");
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final var result2a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.put("{" +
						"\"name\":\"Max " + name + "\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final var value2 = result2a.toObject(Vet.class);
		assertFalse(value1.isEqual(value2));
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals("Max " + name, value2.getName());

		final var result3a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Vet.class)));

		final var result3b = RestApiConnection.with(
						toBackendUrl("/api/vet?name=" + URLEncoder.encode("Max " + name, UTF_8)))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		final var allValue3b = result3b.toCollection(Vet.class);
		assertEquals(1, allValue3b.size());
		assertEquals(1, allValue3b.stream()
				.filter(e -> e.getId().equals(value1.getId()))
				.count());

		final var result3c = RestApiConnection.with(
						toBackendUrl("/api/vet?sort=name,asc"))
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		final var allValue3c = result3c.toCollection(Vet.class);
		assertEquals(6, allValue3c.size());
		assertEquals(1, allValue3c.stream()
				.filter(e -> e.getId().equals(value1.getId()))
				.count());

		final var result4a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));

		final var result4b = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(40)
	void apiVisit() throws Exception {
		final var pet = RestApiConnection.with(
						toBackendUrl("/api/pet?name=" + URLEncoder.encode("Tom", UTF_8)))
				.get().toCollection(Pet.class).getFirst();
		assertNotNull(pet);
		final var vet = RestApiConnection.with(
						toBackendUrl("/api/vet?name=" + URLEncoder.encode("John Cleese", UTF_8)))
				.get().toCollection(Vet.class).getFirst();
		assertNotNull(vet);

		final var result1a = RestApiConnection.with(
						toBackendUrl("/api/visit"))
				.post("{" +
						"\"pet\":\"/api/pet/" + pet.getId() + "\"," +
						"\"vet\":\"/api/vet/" + vet.getId() + "\"," +
						"\"date\":\"2021-04-22\"," +
						"\"text\":\"Lorem ipsum.\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final var value1 = result1a.toObject(Visit.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals(2021, value1.getDate().getYear());
		assertEquals(Month.APRIL, value1.getDate().getMonth());
		assertEquals(22, value1.getDate().getDayOfMonth());
		assertEquals("Lorem ipsum.", value1.getText());

		final var result2a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.put("{" +
						"\"pet\":\"/api/pet/" + pet.getId() + "\"," +
						"\"vet\":\"/api/vet/" + vet.getId() + "\"," +
						"\"date\":\"2021-04-23\"," +
						"\"text\":\"Quia atque.\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final var value2 = result2a.toObject(Visit.class);
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals(2021, value2.getDate().getYear());
		assertEquals(Month.APRIL, value2.getDate().getMonth());
		assertEquals(23, value2.getDate().getDayOfMonth());
		assertEquals("Quia atque.", value2.getText());

		final var result3a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Visit.class)));

		final var result4a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.OK.value()));

		final var result4b = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}
}
