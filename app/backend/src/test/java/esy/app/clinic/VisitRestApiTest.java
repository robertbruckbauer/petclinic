package esy.app.clinic;

import esy.app.EndpointConfiguration;
import esy.app.client.OwnerRepository;
import esy.app.client.PetRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@ContextConfiguration(classes = EndpointConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class VisitRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private VisitRepository visitRepository;

    @BeforeEach
    void setUp(final WebApplicationContext webApplicationContext,
               final RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
                .build();
    }

    @Sql("/sql/owner.sql")
    @Sql("/sql/pet.sql")
    @Sql("/sql/vet.sql")
    @Order(10)
    @Test
    void getApiVisitNoElement() throws Exception {
        assertEquals(0, visitRepository.count());
        mockMvc.perform(get("/api/visit")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0]")
                        .doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-04-20",
            "2021-04-21",
            "2021-04-22"
    })
    @Order(20)
    void postApiVisit(final String date) throws Exception {
        mockMvc.perform(post("/api/visit")
                        .content("{" +
                                "\"pet\":\"/api/pet/c1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"vet\":\"/api/vet/d1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"date\":\"" + date + "\"," +
                                "\"text\":\"Lorem ipsum.\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.id")
                        .isNotEmpty())
                .andExpect(jsonPath("$.date")
                        .value(date))
                .andExpect(jsonPath("$.text")
                        .exists());
    }

    @Test
    @Order(21)
    void postApiVisitConflict() throws Exception {
        mockMvc.perform(post("/api/visit")
                        .content("{" +
                                "\"pet\":\"/api/pet/c1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"vet\":\"/api/vet/d1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"date\":\"2021-04-22\"," +
                                "\"text\":\"Lorem ipsum.\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(30)
    void putApiVisit() throws Exception {
        final String text = "Lorem ipsum";
        final String date = "2021-04-23";
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/visit/" + uuid)
                        .content("{" +
                                "\"pet\":\"/api/pet/c1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"vet\":\"/api/vet/d1111111-1111-beef-dead-beefdeadbeef\"," +
                                "\"date\":\"" + date + "\"," +
                                "\"text\":\"" + text + "\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"0\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.petItem.value")
                        .value("c1111111-1111-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.petItem.text")
                        .value("Dog 'Odi'"))
                .andExpect(jsonPath("$.vetItem.value")
                        .value("d1111111-1111-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.vetItem.text")
                        .value("Alex Fleming"))
                .andExpect(jsonPath("$.date")
                        .value(date))
                .andExpect(jsonPath("$.text")
                        .value(text));
    }

    @Test
    @Order(31)
    void patchApiVisitDate() throws Exception {
        final String date = "2021-04-24";
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/visit/" + uuid)
                        .content("{" +
                                "\"date\":\"" + date + "\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"1\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.date")
                        .value(date));
    }

    @Test
    @Order(32)
    void patchApiVisitText() throws Exception {
        final String text = "At vero eos";
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/visit/" + uuid)
                        .content("{" +
                                "\"text\":\"" + text + "\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"2\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.text")
                        .value(text));
    }

    @Test
    @Order(33)
    void patchApiVisitPet() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/visit/" + uuid)
                        .content("{" +
                                "\"pet\":\"/api/pet/c2222222-2222-beef-dead-beefdeadbeef\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"3\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.petItem.value")
                        .value("c2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.petItem.text")
                        .value("Cat 'Tom'"))
                .andExpect(jsonPath("$.vetItem.value")
                        .value("d1111111-1111-beef-dead-beefdeadbeef"));
    }

    @Test
    @Order(34)
    void getApiVisitPet() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/" + uuid + "/pet")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.id")
                        .value("c2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.name")
                        .value("Tom"));
    }

    @Test
    @Order(35)
    void patchApiVisitVet() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/visit/" + uuid)
                        .content("{" +
                                "\"vet\":\"/api/vet/d2222222-2222-beef-dead-beefdeadbeef\"" +
                                "}")
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"4\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.petItem.value")
                        .value("c2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.petItem.text")
                        .value("Cat 'Tom'"))
                .andExpect(jsonPath("$.vetItem.value")
                        .value("d2222222-2222-beef-dead-beefdeadbeef"));
    }

    @Test
    @Order(36)
    void getApiVisitVet() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/" + uuid + "/vet")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.id")
                        .value("d2222222-2222-beef-dead-beefdeadbeef"))
                .andExpect(jsonPath("$.name")
                        .value("Phillip Dick"));
    }

    @Test
    @Order(40)
    void getApiVisit() throws Exception {
        assertEquals(4, visitRepository.count());
        mockMvc.perform(get("/api/visit/search/findAllByOrderByDateDesc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].date")
                        .value("2021-04-24"))
                .andExpect(jsonPath("$.content[1].date")
                        .value("2021-04-22"))
                .andExpect(jsonPath("$.content[2].date")
                        .value("2021-04-21"))
                .andExpect(jsonPath("$.content[3].date")
                        .value("2021-04-20"))
                .andExpect(jsonPath("$.content[4]")
                        .doesNotExist());
    }

    @Test
    @Order(41)
    void getApiVisitById() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"4\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.date")
                        .value("2021-04-24"));
    }

    @Test
    @Order(42)
    void getApiVisitByIdNotFound() throws Exception {
        final String uuid = "deadbeef-dead-beef-dead-beefdeadbeef";
        assertFalse(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(43)
    void getApiVisitByOwner() throws Exception {
        final String uuid = "b2222222-2222-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/search/findAllByOwner?ownerId=" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].date")
                        .value("2021-04-24"))
                .andExpect(jsonPath("$.content[1].date")
                        .value("2021-04-22"))
                .andExpect(jsonPath("$.content[2].date")
                        .value("2021-04-21"))
                .andExpect(jsonPath("$.content[3].date")
                        .value("2021-04-20"))
                .andExpect(jsonPath("$.content[4]")
                        .doesNotExist());
    }

    @Test
    @Order(44)
    void getApiVisitByPet() throws Exception {
        final String uuid = "c2222222-2222-beef-dead-beefdeadbeef";
        assertTrue(petRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/search/findAllByPet?petId=" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].date")
                        .value("2021-04-24"))
                .andExpect(jsonPath("$.content[1]")
                        .doesNotExist());
    }

    @Test
    @Order(45)
    void getApiVisitByVet() throws Exception {
        final String uuid = "d2222222-2222-beef-dead-beefdeadbeef";
        assertTrue(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/visit/search/findAllByVet?vetId=" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.content")
                        .isArray())
                .andExpect(jsonPath("$.content[0].date")
                        .value("2021-04-24"))
                .andExpect(jsonPath("$.content[1]")
                        .doesNotExist());
    }

    @Test
    @Order(50)
    void deleteApiVisit() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/visit/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiVisitNotFound() throws Exception {
        final String uuid = "e1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(visitRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/visit/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertDoesNotThrow(() -> visitRepository.deleteAll());
        assertDoesNotThrow(() -> vetRepository.deleteAll());
        assertDoesNotThrow(() -> petRepository.deleteAll());
        assertDoesNotThrow(() -> ownerRepository.deleteAll());
    }
}