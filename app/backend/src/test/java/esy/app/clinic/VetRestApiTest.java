package esy.app.clinic;

import esy.app.EndpointConfiguration;
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
class VetRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VetRepository vetRepository;

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

    @Test
    @Order(10)
    void getApiVetNoElement() throws Exception {
        assertEquals(0, vetRepository.count());
        mockMvc.perform(get("/api/vet")
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

    @Test
    @Order(20)
    void postApiVet() throws Exception {
        final String name = "Max Mustermann";
        assertFalse(vetRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/vet")
                        .content("{" +
                                "\"name\":\"" + name + "\"" +
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
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(21)
    void postApiVetConflict() throws Exception {
        final String name = "Max Mustermann";
        assertTrue(vetRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/vet")
                        .content("{" +
                                "\"name\":\"" + name + "\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(30)
    void putApiVet() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/vet/" + uuid)
                        .content("{" +
                                "\"name\":\"" + name + "\"" +
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
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Mia Musterfrau",
            "Bea Musterfrau"
    })
    @Order(31)
    void patchApiVetName(final String name) throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/vet/" + uuid)
                        .content("{" +
                                "\"name\": \"" + name + "\"" +
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
                        .exists("ETag"))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.name")
                        .value(name));
    }

    @Test
    @Order(32)
    void patchApiVetSkill() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/vet/" + uuid)
                        .content("{" +
                                "\"allSkill\": [\"Z\",\"A\"]" +
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
                        .exists("ETag"))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.allSkill")
                        .isArray());
    }

    @Test
    @Order(40)
    void getApiVet() throws Exception {
        assertEquals(2, vetRepository.count());
        mockMvc.perform(get("/api/vet/search/findAllByOrderByNameAsc")
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
                        .exists())
                .andExpect(jsonPath("$.content[1]")
                        .exists())
                .andExpect(jsonPath("$.content[2]")
                        .doesNotExist());
    }

    @Test
    @Order(41)
    void getApiVetItem() throws Exception {
        assertEquals(2, vetRepository.count());
        mockMvc.perform(get("/api/vet/search/findAllItem")
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
                        .exists())
                .andExpect(jsonPath("$.content[1]")
                        .exists())
                .andExpect(jsonPath("$.content[2]")
                        .doesNotExist());
    }

    @Test
    @Order(42)
    void getApiVetById() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/vet/" + uuid)
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
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.allSkill[0]")
                        .value("A"))
                .andExpect(jsonPath("$.allSkill[1]")
                        .value("Z"))
                .andExpect(jsonPath("$.allSkill[2]")
                        .doesNotExist());
    }

    @Test
    @Order(43)
    void getApiVetByIdNotFound() throws Exception {
        final String uuid = "a1111111-ffff-beef-dead-beefdeadbeef";
        assertFalse(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/vet/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(44)
    void getApiVetByName() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        mockMvc.perform(get("/api/vet/search/findByName")
                        .param("name", name)
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
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.allSkill[0]")
                        .value("A"))
                .andExpect(jsonPath("$.allSkill[1]")
                        .value("Z"))
                .andExpect(jsonPath("$.allSkill[2]")
                        .doesNotExist());
    }

    @Test
    @Order(45)
    void getApiVetByNameNotFound() throws Exception {
        final String name = "Mia Musterfrau";
        assertFalse(vetRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/vet/search/findByName")
                        .param("name", name)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(50)
    void deleteApiVet() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/vet/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiVetNotFound() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(vetRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/vet/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertDoesNotThrow(() -> vetRepository.deleteAll());
    }
}