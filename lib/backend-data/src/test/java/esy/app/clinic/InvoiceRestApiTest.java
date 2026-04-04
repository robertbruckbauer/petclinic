package esy.app.clinic;

import esy.api.clinic.Invoice;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
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
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
class InvoiceRestApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

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
    @Order(1)
    void getApiInvoiceNoElement() throws Exception {
        assertEquals(0, invoiceRepository.count());
        mockMvc.perform(get("/api/invoice")
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
    @Order(200)
    void postApiInvoice() throws Exception {
        mockMvc.perform(post("/api/invoice")
                        .content("""
                                {
                                    "at":"2024-01-15",
                                    "due":"2024-02-15",
                                    "status":"D",
                                    "text":"Lorem ipsum."
                                }
                                """)
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
                .andExpect(jsonPath("$.at")
                        .value("2024-01-15"))
                .andExpect(jsonPath("$.due")
                        .value("2024-02-15"))
                .andExpect(jsonPath("$.status")
                        .value("D"))
                .andExpect(jsonPath("$.text")
                        .value("Lorem ipsum."));
    }

    @Test
    @Order(300)
    void putApiInvoice() throws Exception {
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertFalse(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(put("/api/invoice/" + uuid)
                        .content("""
                                {
                                    "at":"2024-03-01",
                                    "due":"2024-04-01",
                                    "status":"I",
                                    "text":"Dolor sit amet."
                                }
                                """)
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
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.at")
                        .value("2024-03-01"))
                .andExpect(jsonPath("$.due")
                        .value("2024-04-01"))
                .andExpect(jsonPath("$.status")
                        .value("I"))
                .andExpect(jsonPath("$.text")
                        .value("Dolor sit amet."));
    }

    @Test
    @Order(400)
    void patchApiInvoiceText() throws Exception {
        final var text = "At vero eos";
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(patch("/api/invoice/" + uuid)
                        .content("""
                                {
                                    "text":"%s"
                                }
                                """.formatted(text))
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
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.text")
                        .value(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"C", "X"})
    @Order(401)
    void patchApiInvoiceStatus(final String status) throws Exception {
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(patch("/api/invoice/" + uuid)
                        .content("""
                                {
                                    "status":"%s"
                                }
                                """.formatted(status))
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
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.status")
                        .value(status));
    }

    @Test
    @Order(402)
    void patchApiInvoiceAt() throws Exception {
        final var at = Invoice.DATE_FORMATTER.format(LocalDate.of(2024, 3, 15));
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(patch("/api/invoice/" + uuid)
                        .content("""
                                {
                                    "at":"%s"
                                }
                                """.formatted(at))
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
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.at")
                        .value(at));
    }

    @Test
    @Order(403)
    void patchApiInvoiceDue() throws Exception {
        final var due = Invoice.DATE_FORMATTER.format(LocalDate.of(2024, 4, 15));
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(patch("/api/invoice/" + uuid)
                        .content("""
                                {
                                    "due":"%s"
                                }
                                """.formatted(due))
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
                        .value(uuid.toString()))
                .andExpect(jsonPath("$.due")
                        .value(due));
    }

    @Test
    @Order(500)
    void getApiInvoice() throws Exception {
        assertEquals(2, invoiceRepository.count());
        mockMvc.perform(get("/api/invoice")
                        .param("sort", "at,asc")
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
    @Order(503)
    void getApiInvoiceById() throws Exception {
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(get("/api/invoice/" + uuid)
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
                        .value(uuid.toString()));
    }

    @Test
    @Order(504)
    void getApiInvoiceByIdNotFound() throws Exception {
        final var uuid = UUID.fromString("e1111111-2222-beef-dead-beefdeadbeef");
        assertFalse(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(get("/api/invoice/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNotFound());
    }

    @Test
    @Order(600)
    void deleteApiInvoice() throws Exception {
        final var uuid = UUID.fromString("e1111111-1111-beef-dead-beefdeadbeef");
        assertTrue(invoiceRepository.findById(uuid).isPresent());
        mockMvc.perform(delete("/api/invoice/" + uuid)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.id")
                        .value(uuid.toString()));
    }

    @Test
    @Order(999)
    @Transactional
    @Rollback(false)
    void cleanup() {
        assertDoesNotThrow(() -> invoiceRepository.deleteAll());
    }
}
