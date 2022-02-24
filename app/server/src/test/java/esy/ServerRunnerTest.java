package esy;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import esy.http.RestApiConnection;
import esy.http.RestApiResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.net.URLEncoder;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ServerRunnerTest {

	@LocalServerPort
	private int port;

	String toBackendUrl(final String path) {
		return "http://localhost:" + port + path;
	}

	@Test
	@Order(1)
	void health() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/actuator/health")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertThat(result.toString(),
				equalTo("{\"status\":\"UP\",\"groups\":[\"liveness\",\"readiness\"]}"));
	}

	@Test
	@Order(2)
	void version() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/version")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Map<?,?> allValue = result.toObject(Map.class);
		assertEquals(2, allValue.size());
		assertTrue(allValue.containsKey("major"));
		assertTrue(allValue.containsKey("minor"));
	}

	@Test
	@Order(3)
	void apiEnumSkill() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/api/enum/skill")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertEquals(3, result.toCollection(Enum.class).size());
	}

	@Test
	@Order(4)
	void apiEnumSpecies() throws Exception {
		final RestApiResult result = RestApiConnection.with(
				toBackendUrl("/api/enum/species")).get();
		assertThat(result.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertEquals(5, result.toCollection(Enum.class).size());
	}

	@Test
	@Order(10)
	void apiOwner() throws Exception {
		final String name = "Mustermann";

		final RestApiResult result1a = RestApiConnection.with(
				toBackendUrl("/api/owner"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"," +
						"\"address\":\"110 Space Way\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final Owner value1 = result1a.toObject(Owner.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals("Alf " + name, value1.getName());

		final RestApiResult result1b = RestApiConnection.with(
				toBackendUrl("/api/owner"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"," +
						"\"address\":\"110 Space Way\"" +
						"}");
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result2a = RestApiConnection.with(
				toBackendUrl("/api/owner/" + value1.getId()))
				.put("{" +
						"\"name\":\"Max " + name + "\"," +
						"\"address\":\"2749 Blackhawk Trail\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Owner value2 = result2a.toObject(Owner.class);
		assertFalse(value1.isEqual(value2));
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals("Max " + name, value2.getName());

		final RestApiResult result3a = RestApiConnection.with(
				toBackendUrl("/api/owner/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Owner.class)));

		final RestApiResult result3b = RestApiConnection.with(
				toBackendUrl("/api/owner/search/findByName?name=" + URLEncoder.encode("Max " + name, UTF_8)))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3b.toObject(Owner.class)));

		final RestApiResult result3c = RestApiConnection.with(
				toBackendUrl("/api/owner/search/findAllByOrderByNameAsc"))
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<Owner> allValue2a = result3c.toCollection(Owner.class);
		assertEquals(5, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getId().equals(value1.getId()))
				.count());

		final RestApiResult result4a = RestApiConnection.with(
				toBackendUrl("/api/owner/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
				toBackendUrl("/api/owner/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(20)
	void apiPet() throws Exception {
		final Owner owner = RestApiConnection.with(
						toBackendUrl("/api/owner/search/findByName?name=" + URLEncoder.encode("Thomas Mann", UTF_8)))
				.get().toObject(Owner.class);
		assertNotNull(owner);

		final RestApiResult result1a = RestApiConnection.with(
						toBackendUrl("/api/pet"))
				.post("{" +
						"\"owner\":\"/api/owner/" + owner.getId() + "\"," +
						"\"name\":\"Alf\"," +
						"\"born\":\"2021-04-22\"," +
						"\"species\":\"Alien\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final Pet value1 = result1a.toObject(Pet.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals("Alf", value1.getName());
		assertEquals(2021, value1.getBorn().getYear());
		assertEquals(Month.APRIL, value1.getBorn().getMonth());
		assertEquals(22, value1.getBorn().getDayOfMonth());
		assertEquals("Alien", value1.getSpecies());

		final RestApiResult result1b = RestApiConnection.with(
						toBackendUrl("/api/pet"))
				.post("{" +
						"\"owner\":\"/api/owner/" + owner.getId() + "\"," +
						"\"name\":\"Alf\"," +
						"\"born\":\"2021-04-22\"," +
						"\"species\":\"Alien\"" +
						"}");
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result2a = RestApiConnection.with(
						toBackendUrl("/api/pet/" + value1.getId()))
				.put("{" +
						"\"owner\":\"/api/owner/" + owner.getId() + "\"," +
						"\"name\":\"Max\"," +
						"\"born\":\"2021-04-22\"," +
						"\"species\":\"Rat\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Pet value2 = result2a.toObject(Pet.class);
		assertFalse(value1.isEqual(value2));
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals("Max", value2.getName());
		assertEquals(2021, value2.getBorn().getYear());
		assertEquals(Month.APRIL, value2.getBorn().getMonth());
		assertEquals(22, value2.getBorn().getDayOfMonth());
		assertEquals("Rat", value2.getSpecies());

		final RestApiResult result3a = RestApiConnection.with(
						toBackendUrl("/api/pet/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Pet.class)));

		final RestApiResult result3b = RestApiConnection.with(
						toBackendUrl("/api/pet/search/findByName?name=" + URLEncoder.encode("Max", UTF_8)))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3b.toObject(Pet.class)));

		final RestApiResult result3c = RestApiConnection.with(
						toBackendUrl("/api/pet/search/findAllByOrderByNameAsc"))
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<Pet> allValue2a = result3c.toCollection(Pet.class);
		assertEquals(4, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getId().equals(value1.getId()))
				.count());

		final RestApiResult result4a = RestApiConnection.with(
						toBackendUrl("/api/pet/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
						toBackendUrl("/api/pet/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(30)
	void apiVet() throws Exception {
		final String name = "Mustermann";

		final RestApiResult result1a = RestApiConnection.with(
						toBackendUrl("/api/vet"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final Vet value1 = result1a.toObject(Vet.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals("Alf " + name, value1.getName());

		final RestApiResult result1b = RestApiConnection.with(
						toBackendUrl("/api/vet"))
				.post("{" +
						"\"name\":\"Alf " + name + "\"" +
						"}");
		assertThat(result1b.getCode(),
				equalTo(HttpStatus.CONFLICT.value()));

		final RestApiResult result2a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.put("{" +
						"\"name\":\"Max " + name + "\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Vet value2 = result2a.toObject(Vet.class);
		assertFalse(value1.isEqual(value2));
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals("Max " + name, value2.getName());

		final RestApiResult result3a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Vet.class)));

		final RestApiResult result3b = RestApiConnection.with(
						toBackendUrl("/api/vet/search/findByName?name=" + URLEncoder.encode("Max " + name, UTF_8)))
				.get();
		assertThat(result3b.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3b.toObject(Vet.class)));

		final RestApiResult result3c = RestApiConnection.with(
						toBackendUrl("/api/vet/search/findAllByOrderByNameAsc"))
				.get();
		assertThat(result3c.getCode(),
				equalTo(HttpStatus.OK.value()));
		final List<Vet> allValue2a = result3c.toCollection(Vet.class);
		assertEquals(6, allValue2a.size());
		assertEquals(1, allValue2a.stream()
				.filter(e -> e.getId().equals(value1.getId()))
				.count());

		final RestApiResult result4a = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
						toBackendUrl("/api/vet/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}

	@Test
	@Order(40)
	void apiVisit() throws Exception {
		final Pet pet = RestApiConnection.with(
						toBackendUrl("/api/pet/search/findByName?name=" + URLEncoder.encode("Tom", UTF_8)))
				.get().toObject(Pet.class);
		assertNotNull(pet);
		final Vet vet = RestApiConnection.with(
						toBackendUrl("/api/vet/search/findByName?name=" + URLEncoder.encode("John Cleese", UTF_8)))
				.get().toObject(Vet.class);
		assertNotNull(vet);

		final RestApiResult result1a = RestApiConnection.with(
						toBackendUrl("/api/visit"))
				.post("{" +
						"\"pet\":\"/api/pet/" + pet.getId() + "\"," +
						"\"vet\":\"/api/vet/" + vet.getId() + "\"," +
						"\"date\":\"2021-04-22\"," +
						"\"text\":\"Lorem ipsum.\"" +
						"}");
		assertThat(result1a.getCode(),
				equalTo(HttpStatus.CREATED.value()));
		final Visit value1 = result1a.toObject(Visit.class);
		assertEquals(0L, value1.getVersion());
		assertNotNull(value1.getId());
		assertEquals(2021, value1.getDate().getYear());
		assertEquals(Month.APRIL, value1.getDate().getMonth());
		assertEquals(22, value1.getDate().getDayOfMonth());
		assertEquals("Lorem ipsum.", value1.getText());

		final RestApiResult result2a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.put("{" +
						"\"pet\":\"/api/pet/" + pet.getId() + "\"," +
						"\"vet\":\"/api/vet/" + vet.getId() + "\"," +
						"\"date\":\"2021-04-23\"," +
						"\"text\":\"Quia atque.\"" +
						"}");
		assertThat(result2a.getCode(),
				equalTo(HttpStatus.OK.value()));
		final Visit value2 = result2a.toObject(Visit.class);
		assertEquals(1L, value2.getVersion());
		assertNotNull(value2.getId());
		assertEquals(2021, value2.getDate().getYear());
		assertEquals(Month.APRIL, value2.getDate().getMonth());
		assertEquals(23, value2.getDate().getDayOfMonth());
		assertEquals("Quia atque.", value2.getText());

		final RestApiResult result3a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value2.getId()))
				.get();
		assertThat(result3a.getCode(),
				equalTo(HttpStatus.OK.value()));
		assertTrue(value2.isEqual(result3a.toObject(Visit.class)));

		final RestApiResult result4a = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.delete();
		assertThat(result4a.getCode(),
				equalTo(HttpStatus.NO_CONTENT.value()));

		final RestApiResult result4b = RestApiConnection.with(
						toBackendUrl("/api/visit/" + value1.getId()))
				.delete();
		assertThat(result4b.getCode(),
				equalTo(HttpStatus.NOT_FOUND.value()));
	}
}
