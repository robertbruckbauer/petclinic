package esy.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith({MockitoExtension.class})
class EsyBackendExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    static Stream<HttpMethod> error() {
        return Stream.of(
                HttpMethod.GET,
                HttpMethod.HEAD,
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.PATCH,
                HttpMethod.DELETE);
    }

    @ParameterizedTest
    @MethodSource
    void error(final HttpMethod method) throws Exception {
        final var uri = URI.create("/error");
        final var jsonString = mockMvc.perform(request(method, uri))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.OK.value(), jsonObject.getStatus());
        assertNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{,}",
            "<.>"
    })
    void withBadJson(final String json) throws Exception {
        final var uri = URI.create("/badJson");
        final var jsonString = mockMvc.perform(get(uri)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.BAD_REQUEST.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 500, 501})
    void withClientError(final int code) throws Exception {
        final var uri = URI.create("/httpClientErrorException/" + code);
        final var jsonString = mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status()
                        .is(code))
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(code, jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 500, 501})
    void withServerError(final int code) throws Exception {
        final var uri = URI.create("/httpServerErrorException/" + code);
        final var jsonString = mockMvc.perform(get(uri))
                .andDo(print())
                .andExpect(status()
                        .is(code))
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(code, jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/badCredentialsException"
    })
    void statusUnauthorized(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isUnauthorized())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/accessDeniedException"
    })
    void statusForbidden(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isForbidden())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.FORBIDDEN.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/illegalArgumentException",
            "/illegalStateException",
            "/numberFormatException"
    })
    void statusBadRequest(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.BAD_REQUEST.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/dataRetrievalFailureException",
            "/entityNotFoundException",
            "/resourceAccessException",
            "/missingResourceException",
            "/noSuchElementException",
            "/noSuchFileException",
            "/fileNotFoundException"
    })
    void statusNotFound(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isNotFound())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.NOT_FOUND.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/dataIntegrityViolationException",
            "/concurrencyFailureException",
            "/entityExistsException",
            "/optimisticLockException",
            "/pessimisticLockException"
    })
    void statusConflict(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isConflict())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.CONFLICT.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/etagDoesntMatchException"
    })
    void statusPreconditionFailed(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isPreconditionFailed())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.PRECONDITION_FAILED.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/badSqlGrammarException",
            "/unsupportedEncodingException",
            "/unsupportedOperationException"
    })
    void statusNotImplemented(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isNotImplemented())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.NOT_IMPLEMENTED.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/nullPointerException"
    })
    void statusInternalServerError(final String path) throws Exception {
        final var uri = URI.create(path);
        final var jsonString = mockMvc.perform(get(uri)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + uri))
                .andDo(print())
                .andExpect(status()
                        .isInternalServerError())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var jsonObject = jsonMapper.readValue(jsonString, ProblemDetail.class);
        assertNotNull(jsonObject);
        assertEquals(uri, jsonObject.getInstance());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), jsonObject.getStatus());
        assertNotNull(jsonObject.getDetail());
        assertNull(jsonObject.getProperties());
    }
}
