package esy.app.info;

import esy.app.EndpointConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class EnumRestApiTest {

    static final String ENUM_ART = "TEST";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnumRepository enumRepository;

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
    void getApiEnumNoElements() throws Exception {
        mockMvc.perform(get("/api/enum/" + ENUM_ART)
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
    @CsvSource({
            "0,A,Alpha",
            "1,B,Beta",
            "2,C,Gamma"
    })
    @Order(20)
    void postApiEnum(final String code, final String name, final String text) throws Exception {
        mockMvc.perform(post("/api/enum/" + ENUM_ART)
                        .content("{" +
                                "\"code\":\"" + code + "\"," +
                                "\"name\":\"" + name +"\"," +
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
                        .exists("Vary"))
                .andExpect(jsonPath("$.value")
                        .value(name))
                .andExpect(jsonPath("$.code")
                        .value(code))
                .andExpect(jsonPath("$.name")
                        .value(name))
                .andExpect(jsonPath("$.text")
                        .value(text));
    }

    @Test
    @Order(21)
    void postApiEnumConflict() throws Exception {
        mockMvc.perform(post("/api/enum/" + ENUM_ART)
                        .content("{" +
                                "\"code\":\"0\"," +
                                "\"name\":\"A\"," +
                                "\"text\":\"Alpha\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(30)
    void putApiEnum() throws Exception {
        mockMvc.perform(put("/api/enum/" + ENUM_ART + "/0")
                        .content("{" +
                                "\"code\":\"0\"," +
                                "\"name\":\"A1\"," +
                                "\"text\":\"Alpha Eins\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
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
                .andExpect(jsonPath("$.value")
                        .value("A1"))
                .andExpect(jsonPath("$.code")
                        .value("0"))
                .andExpect(jsonPath("$.name")
                        .value("A1"))
                .andExpect(jsonPath("$.text")
                        .value("Alpha Eins"));
    }

    @Test
    @Order(31)
    void putApiEnumConflict() throws Exception {
        mockMvc.perform(put("/api/enum/" + ENUM_ART + "/0")
                        .content("{" +
                                "\"code\":\"0\"," +
                                "\"name\":\"B\"," +
                                "\"text\":\"Alpha\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isConflict());
    }

    @Test
    @Order(32)
    void putApiEnumNotFound() throws Exception {
        mockMvc.perform(put("/api/enum/" + ENUM_ART + "/26")
                        .content("{" +
                                "\"code\":\"26\"," +
                                "\"name\":\"Z\"," +
                                "\"text\":\"Zeta\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(40)
    void getApiEnum() throws Exception {
        mockMvc.perform(get("/api/enum/" + ENUM_ART)
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
                .andExpect(jsonPath("$.content[0].value")
                        .value("A1"))
                .andExpect(jsonPath("$.content[0].code")
                        .value("0"))
                .andExpect(jsonPath("$.content[0].name")
                        .value("A1"))
                .andExpect(jsonPath("$.content[0].text")
                        .value("Alpha Eins"))
                .andExpect(jsonPath("$.content[1].value")
                        .value("B"))
                .andExpect(jsonPath("$.content[1].code")
                        .value("1"))
                .andExpect(jsonPath("$.content[1].name")
                        .value("B"))
                .andExpect(jsonPath("$.content[1].text")
                        .value("Beta"))
                .andExpect(jsonPath("$.content[2].value")
                        .value("C"))
                .andExpect(jsonPath("$.content[2].code")
                        .value("2"))
                .andExpect(jsonPath("$.content[2].name")
                        .value("C"))
                .andExpect(jsonPath("$.content[2].text")
                        .value("Gamma"))
                .andExpect(jsonPath("$.content[3]")
                        .doesNotExist());
    }

    @Test
    @Order(99)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertEquals(3, enumRepository.count(ENUM_ART));
        enumRepository.findAll(ENUM_ART).forEach(e ->
                enumRepository.delete(e));
        enumRepository.deleteAll();
    }
}
