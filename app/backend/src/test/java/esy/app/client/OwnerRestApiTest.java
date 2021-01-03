package esy.app.client;

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
class OwnerRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OwnerRepository ownerRepository;

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
    void getApiOwnerNoElement() throws Exception {
        assertEquals(0, ownerRepository.count());
        mockMvc.perform(get("/api/owner")
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
    void postApiOwner() throws Exception {
        final String name = "Max Mustermann";
        assertFalse(ownerRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/owner")
                        .content("{" +
                                "\"name\":\"" + name + "\"," +
                                "\"address\":\"Bergweg 1, 5400 Hallein\"," +
                                "\"contact\":\"+43 6452 4456\"" +
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
                        .value(name))
                .andExpect(jsonPath("$.address")
                        .isNotEmpty())
                .andExpect(jsonPath("$.contact")
                        .isNotEmpty());
    }

    @Test
    @Order(21)
    void postApiOwnerConflict() throws Exception {
        final String name = "Max Mustermann";
        assertTrue(ownerRepository.findByName(name).isPresent());
        mockMvc.perform(post("/api/owner")
                        .content("{" +
                                "\"name\":\"" + name + "\"," +
                                "\"address\":\"Bergweg 1, 5400 Hallein\"," +
                                "\"contact\":\"+43 6452 4456\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(30)
    void putApiOwner() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(put("/api/owner/" + uuid)
                        .content("{" +
                                "\"name\":\"" + name + "\"," +
                                "\"address\":\"Bergweg 1, 5400 Hallein\"," +
                                "\"contact\":\"+43 6452 4456\"" +
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
                        .value(name))
                .andExpect(jsonPath("$.address")
                        .isNotEmpty())
                .andExpect(jsonPath("$.contact")
                        .isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Mia Musterfrau",
            "Bea Musterfrau"
    })
    @Order(31)
    void patchApiOwnerName(final String name) throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/owner/" + uuid)
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

    @ParameterizedTest
    @ValueSource(strings = {
            "Domplatz 2, 5020 Salzburg",
            "Bergweg 1, 5400 Hallein"
    })
    @Order(32)
    void patchApiOwnerAddress(final String address) throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/owner/" + uuid)
                        .content("{" +
                                "\"address\": \"" + address + "\"" +
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
                .andExpect(jsonPath("$.address")
                        .value(address));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "me@we.com",
            "+43 6452 4456"
    })
    @Order(33)
    void patchApiOwnerContact(final String contact) throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(patch("/api/owner/" + uuid)
                        .content("{" +
                                "\"contact\": \"" + contact + "\"" +
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
                .andExpect(jsonPath("$.contact")
                        .value(contact));
    }

    @Test
    @Order(40)
    void getApiOwner() throws Exception {
        assertEquals(2, ownerRepository.count());
        mockMvc.perform(get("/api/owner/search/findAllByOrderByNameAsc")
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
    void getApiOwnerItem() throws Exception {
        assertEquals(2, ownerRepository.count());
        mockMvc.perform(get("/api/owner/search/findAllItem")
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
    void getApiOwnerById() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/owner/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .string("ETag", "\"6\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.address")
                        .isNotEmpty());
    }

    @Test
    @Order(43)
    void getApiOwnerByIdNotFound() throws Exception {
        final String uuid = "a1111111-ffff-beef-dead-beefdeadbeef";
        assertFalse(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(get("/api/owner/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(44)
    void getApiOwnerByName() throws Exception {
        final String name = "Bea Musterfrau";
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        mockMvc.perform(get("/api/owner/search/findByName")
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
                        .string("ETag", "\"6\""))
                .andExpect(jsonPath("$.id")
                        .value(uuid))
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.address")
                        .isNotEmpty());
    }

    @Test
    @Order(45)
    void getApiOwnerByNameNotFound() throws Exception {
        final String name = "Mia Musterfrau";
        assertFalse(ownerRepository.findByName(name).isPresent());
        mockMvc.perform(get("/api/owner/search/findByName")
                        .param("name", name)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(50)
    void deleteApiOwner() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertTrue(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/owner/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    @Order(51)
    void deleteApiOwnerNotFound() throws Exception {
        final String uuid = "a1111111-1111-beef-dead-beefdeadbeef";
        assertFalse(ownerRepository.findById(UUID.fromString(uuid)).isPresent());
        mockMvc.perform(delete("/api/owner/" + uuid))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertDoesNotThrow(() -> ownerRepository.deleteAll());
    }
}