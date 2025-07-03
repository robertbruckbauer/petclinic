package esy.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class EsyBackendRestApiTest {

    @Autowired
    private MockMvc mockMvc;

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
    void getBaseNotFound() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/actuator/mappings",
            "/actuator/liquibase",
            "/actuator/sessions"
    })
    void getPageNotFound(final String path) throws Exception {
        // Disabled in printer.properties
        mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    void getHealthz() throws Exception {
        mockMvc.perform(get("/healthz"))
                .andDo(print())
                .andExpect(status()
                        .isOk());
    }

    @Test
    void getHealth() throws Exception {
        // Enabled in endpoint.properties
        mockMvc.perform(get("/actuator/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .value("UP"));
    }

    @Test
    void getHealthLiveness() throws Exception {
        // Enabled in endpoint.properties
        mockMvc.perform(get("/actuator/health/liveness")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .value("UP"));
    }

    @Test
    void getHealthReadiness() throws Exception {
        // Enabled in endpoint.properties
        mockMvc.perform(get("/actuator/health/readiness")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .value("UP"));
    }

    @Test
    void home() throws Exception {
        mockMvc.perform(get("/home"))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    void getVersionJson() throws Exception {
        mockMvc.perform(get("/version")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.major")
                        .isNotEmpty())
                .andExpect(jsonPath("$.minor")
                        .isNotEmpty())
                .andExpect(jsonPath("$.version")
                        .isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/version",
            "/version.adoc"
    })
    void getVersionAdoc(final String uri) throws Exception {
        final var adoc = mockMvc.perform(get(uri)
                        .accept(MediaType.parseMediaType("text/asciidoc")))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType("text/asciidoc;charset=UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(adoc);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/version",
            "/version.html"
    })
    void getVersionHtml(final String uri) throws Exception {
        final var html = mockMvc.perform(get(uri)
                        .accept(MediaType.parseMediaType("text/html")))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType("text/html;charset=UTF-8"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotNull(html);
        assertTrue(html.startsWith("<span id=\"version\">"));
        assertTrue(html.endsWith("</span><br/>\n"));
    }
}
