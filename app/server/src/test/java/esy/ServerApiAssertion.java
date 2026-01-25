package esy;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.basis.Enum;
import esy.rest.JsonJpaMapper;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class ServerApiAssertion {

    private final JsonJpaMapper jsonMapper = new JsonJpaMapper();

    private final APIRequestContext context;

    public ServerApiAssertion(@NonNull final Playwright playwright, @NonNull final String baseUrl) {
        this.context = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl)
                .setTimeout(Duration.ofMinutes(5L).toMillis())
                .setExtraHTTPHeaders(Map.of(
                        HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE
                )));
    }

    private <T> T doWithApi(@NonNull final Function<APIRequestContext, APIResponse> operation, @NonNull final Function<APIResponse, T> converter) {
        return converter.apply(operation.apply(context));
    }

    private static String randomName(@NonNull final String nameTemplate) {
        final var name = UUID.randomUUID().toString()
                .replace("-", "")
                .replaceAll("\\d", "");
        return nameTemplate.formatted(name);
    }

    @SuppressWarnings({"java:S2245", "java:S2119"}) // safe, no reuse
    protected static int randomInt(int min, int max) {
        // start from min to max inclusive
        return new Random().nextInt(max) + min;
    }

    public void assertEnumSkill() {
        final var allEnum = doWithApi(
                (api) -> api.get("/api/enum/skill"),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    return jsonMapper.parseJsonContent(res.text(), Enum.class);
                });
        assertFalse(allEnum.isEmpty());
        final var allEnumName = allEnum.stream().map(Enum::getName).toList();
        assertTrue(allEnumName.contains("Radiology"));
        assertTrue(allEnumName.contains("Dentistry"));
        assertTrue(allEnumName.contains("Surgery"));
    }

    public void assertEnumSpecies() {
        final var allEnum = doWithApi(
                (api) -> api.get("/api/enum/species"),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    return jsonMapper.parseJsonContent(res.text(), Enum.class);
                });
        assertFalse(allEnum.isEmpty());
        final var allEnumName = allEnum.stream().map(Enum::getName).toList();
        assertTrue(allEnumName.contains("Cat"));
        assertTrue(allEnumName.contains("Dog"));
        assertTrue(allEnumName.contains("Rat"));
        assertTrue(allEnumName.contains("Pig"));
        assertTrue(allEnumName.contains("Bird"));
    }

    private Owner createOwner() {
        final var name = randomName("Alf M%s");
        final var address = randomName("S%s 3, 5400 Rif");
        final var contact = randomName("m%s@a.de");
        return doWithApi(
                (api) -> api.post("/api/owner", RequestOptions.create()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setData("""
                                {
                                    "name":"%s",
                                    "address":"%s",
                                    "contact":"%s"
                                }
                                """.formatted(name, address, contact))),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.CREATED.value()));
                    final var json = Owner.fromJson(res.text());
                    assertNotNull(json.getId());
                    assertEquals(name, json.getName());
                    assertEquals(address, json.getAddress());
                    assertEquals(contact, json.getContact());
                    return json;
                });
    }

    private void deleteOwner(@NonNull final Owner owner) {
        doWithApi(
                (api) -> api.delete("/api/owner/%s".formatted(owner.getId())),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var json = Owner.fromJson(res.text());
                    assertEquals(owner.getId(), json.getId());
                    return null;
                });
    }

    public void assertOwner() {
        final var owner = createOwner();
        try {
            doWithApi(
                    (api) -> api.get("/api/owner", RequestOptions.create()
                            .setQueryParam("name", owner.getName())),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var jsonReader = jsonMapper.parseJsonPath(res.text());
                        final var allId = jsonReader.readContent("id");
                        assertEquals(1, allId.size());
                        assertEquals(owner.getId(), UUID.fromString(allId.getFirst()));
                        return null;
                    });
            doWithApi(
                    (api) -> api.patch("/api/owner/%s".formatted(owner.getId()), RequestOptions.create()
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json")
                            .setData("""
                                    {
                                        "address":"X%s"
                                    }
                                    """.formatted(owner.getAddress()))),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var json = Owner.fromJson(res.text());
                        assertEquals(owner.getId(), json.getId());
                        assertNotEquals(owner.getAddress(), json.getAddress());
                        return json.getId();
                    });
            doWithApi(
                    (api) -> api.patch("/api/owner/%s".formatted(owner.getId()), RequestOptions.create()
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json")
                            .setData("""
                                    {
                                        "contact":"X%s"
                                    }
                                    """.formatted(owner.getContact()))),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var json = Owner.fromJson(res.text());
                        assertEquals(owner.getId(), json.getId());
                        assertNotEquals(owner.getContact(), json.getContact());
                        return json.getId();
                    });
        } finally {
            deleteOwner(owner);
        }
    }

    private Pet createPet(@NonNull final Owner owner, @NonNull final String species) {
        final var name = randomName("Alf M%s");
        final var born = LocalDate.of(2021, 10, randomInt(1, 31));
        return doWithApi(
                (api) -> api.post("/api/pet", RequestOptions.create()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setData("""
                                {
                                    "owner":"/api/owner/%s",
                                    "name":"%s",
                                    "species":"%s",
                                    "born":"%s"
                                }
                                """.formatted(owner.getId(), name, species, born))),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.CREATED.value()));
                    final var json = Pet.fromJson(res.text());
                    assertNull(json.getOwner());
                    assertNotNull(json.getId());
                    assertEquals(name, json.getName());
                    assertEquals(species, json.getSpecies());
                    assertEquals(born, json.getBorn());
                    return json;
                });
    }

    private void deletePet(@NonNull final Pet pet) {
        doWithApi(
                (api) -> api.delete("/api/pet/%s".formatted(pet.getId())),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var json = Pet.fromJson(res.text());
                    assertEquals(pet.getId(), json.getId());
                    return null;
                });
    }

    public void assertPet() {
        final var owner = createOwner();
        try {
            List.of("Dog", "Cat").forEach(species -> {
                final var pet = createPet(owner, species);
                try {
                    doWithApi(
                            (api) -> api.get("/api/pet", RequestOptions.create()
                                    .setQueryParam("name", pet.getName())),
                            (res) -> {
                                assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                                final var jsonReader = jsonMapper.parseJsonPath(res.text());
                                final var allId = jsonReader.readContent("id");
                                assertEquals(1, allId.size());
                                assertEquals(pet.getId(), UUID.fromString(allId.getFirst()));
                                return null;
                            });
                    doWithApi(
                            (api) -> api.get("/api/pet/%s/owner".formatted(pet.getId())),
                            (res) -> {
                                assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                                final var json = Owner.fromJson(res.text());
                                assertEquals(owner.getId(), json.getId());
                                return json.getId();
                            });
                    doWithApi(
                            (api) -> api.patch("/api/pet/%s".formatted(pet.getId()), RequestOptions.create()
                                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json")
                                    .setData("""
                                            {
                                                "born":"%s"
                                            }
                                            """.formatted(pet.getBorn().plusDays(1L)))),
                            (res) -> {
                                assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                                final var json = Pet.fromJson(res.text());
                                assertEquals(pet.getId(), json.getId());
                                assertNotEquals(pet.getBorn(), json.getBorn());
                                return json.getId();
                            });
                } finally {
                    deletePet(pet);
                }
            });
        } finally {
            deleteOwner(owner);
        }
    }

    private Vet createVet(@NonNull final String skill) {
        final var name = randomName("Doc M%s");
        return doWithApi(
                (api) -> api.post("/api/vet", RequestOptions.create()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setData("""
                                {
                                    "name":"%s",
                                    "allSkill":["%s"]
                                }
                                """.formatted(name, skill))),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.CREATED.value()));
                    final var json = Vet.fromJson(res.text());
                    assertNotNull(json.getId());
                    assertEquals(name, json.getName());
                    assertTrue(json.getAllSkill().contains(skill));
                    return json;
                });
    }

    private void deleteVet(@NonNull final Vet vet) {
        doWithApi(
                (api) -> api.delete("/api/vet/%s".formatted(vet.getId())),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var json = Vet.fromJson(res.text());
                    assertEquals(vet.getId(), json.getId());
                    return null;
                });
    }

    public void assertVet() {
        List.of("Radiology", "Surgery").forEach(skill -> {
            final var vet = createVet(skill);
            try {
                doWithApi(
                        (api) -> api.get("/api/vet", RequestOptions.create()
                                .setQueryParam("name", vet.getName())),
                        (res) -> {
                            assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                            final var jsonReader = jsonMapper.parseJsonPath(res.text());
                            final var allId = jsonReader.readContent("id");
                            assertEquals(1, allId.size());
                            assertEquals(vet.getId(), UUID.fromString(allId.getFirst()));
                            return null;
                        });
                doWithApi(
                        (api) -> api.patch("/api/vet/%s".formatted(vet.getId()), RequestOptions.create()
                                .setHeader(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json")
                                .setData("""
                                        {
                                            "allSkill":[]
                                        }
                                        """)),
                        (res) -> {
                            assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                            final var json = Vet.fromJson(res.text());
                            assertEquals(vet.getId(), json.getId());
                            assertTrue(json.getAllSkill().isEmpty());
                            return json.getId();
                        });
            } finally {
                deleteVet(vet);
            }
        });
    }

    private void deleteVisit(@NonNull final Visit visit) {
        doWithApi(
                (api) -> api.delete("/api/visit/%s".formatted(visit.getId())),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var json = Visit.fromJson(res.text());
                    assertEquals(visit.getId(), json.getId());
                    return null;
                });
    }

    private Visit createVisit(@NonNull final LocalDate date, @NonNull final Pet pet, @NonNull final Vet vet) {
        return doWithApi(
                (api) -> api.post("/api/visit", RequestOptions.create()
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setData("""
                                {
                                    "pet":"/api/pet/%s",
                                    "vet":"/api/vet/%s",
                                    "date":"%s"
                                }""".formatted(pet.getId(), vet.getId(), date))),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.CREATED.value()));
                    final var json = Visit.fromJson(res.text());
                    assertNull(json.getPet());
                    assertNull(json.getVet());
                    assertNotNull(json.getId());
                    assertEquals(date, json.getDate());
                    assertEquals("", json.getText());
                    return json;
                });
    }

    public void assertVisit() {
        final var pet = doWithApi(
                (api) -> api.get("/api/pet", RequestOptions.create()
                        .setQueryParam("name", "Odi")),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var allPet = jsonMapper.parseJsonContent(res.text(), Pet.class);
                    assertEquals(1, allPet.size());
                    return allPet.getFirst();
                });

        final var vet = doWithApi(
                (api) -> api.get("/api/vet", RequestOptions.create()
                        .setQueryParam("name", "Eric Idle")),
                (res) -> {
                    assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                    final var allVet = jsonMapper.parseJsonContent(res.text(), Vet.class);
                    assertEquals(1, allVet.size());
                    return allVet.getFirst();
                });

        final var visitDate = LocalDate.now();
        final var visitText = "Lorem ipsum.";
        final var visit = createVisit(visitDate, pet, vet);
        try {
            doWithApi(
                    (api) -> api.get("/api/visit/%s/pet".formatted(visit.getId())),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var json = Pet.fromJson(res.text());
                        assertEquals(pet.getId(), json.getId());
                        return json.getId();
                    });
            doWithApi(
                    (api) -> api.get("/api/visit/%s/vet".formatted(visit.getId())),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var json = Vet.fromJson(res.text());
                        assertEquals(vet.getId(), json.getId());
                        return json.getId();
                    });
            doWithApi(
                    (api) -> api.patch("/api/visit/%s".formatted(visit.getId()), RequestOptions.create()
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/merge-patch+json")
                            .setData("""
                                        {
                                            "text":"%s"
                                        }
                                        """.formatted(visitText))),
                    (res) -> {
                        assertThat(res.status(), equalTo(HttpStatus.OK.value()));
                        final var json = Visit.fromJson(res.text());
                        assertEquals(visit.getId(), json.getId());
                        assertEquals(visitDate, json.getDate());
                        assertEquals(visitText, json.getText());
                        return json.getId();
                    });
        } finally {
            deleteVisit(visit);
        }
    }
}
