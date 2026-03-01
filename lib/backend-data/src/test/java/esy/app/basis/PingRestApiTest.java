package esy.app.basis;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class PingRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PingRepository pingRepository;

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
    void getApiPingNotAllowed() throws Exception {
        mockMvc.perform(get("/api/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isMethodNotAllowed());
    }

    @Test
    @Order(20)
    void postApiPingNotAllowed() throws Exception {
        mockMvc.perform(post("/api/ping")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isMethodNotAllowed());
    }

    @Test
    @Order(30)
    void putApiPing() throws Exception {
        final var uuid = UUID.fromString("a1234567-dead-beef-dead-beefdeadbeef");
        mockMvc.perform(put("/api/ping/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isCreated())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.at")
                        .exists());
    }

    @Test
    @Order(31)
    void putApiPingAgain() throws Exception {
        final var uuid = UUID.fromString("a1234567-dead-beef-dead-beefdeadbeef");
        mockMvc.perform(put("/api/ping/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.at")
                        .exists());
    }

    @Test
    @Order(40)
    void getApiPingById() throws Exception {
        final var uuid = UUID.fromString("a1234567-dead-beef-dead-beefdeadbeef");
        mockMvc.perform(get("/api/ping/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(header()
                        .exists("Vary"))
                .andExpect(jsonPath("$.at")
                        .exists());
    }

    @Test
    @Order(50)
    void deleteApiPingNotAllowed() throws Exception {
        final var uuid = UUID.fromString("a1234567-dead-beef-dead-beefdeadbeef");
        mockMvc.perform(delete("/api/ping/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isMethodNotAllowed());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertEquals(1, pingRepository.count());
        pingRepository.deleteAll(pingRepository.findAll());
    }
}
