package esy.app;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.stream.Stream;

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
        mockMvc.perform(request(method, URI.create("/error")))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{,}",
            "<.>"
    })
    void withBadJson(final String json) throws Exception {
        mockMvc.perform(get("/badJson")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 500, 501})
    void withClientError(final int code) throws Exception {
        mockMvc.perform(get("/httpClientErrorException/" + code))
                .andDo(print())
                .andExpect(status()
                        .is(code))
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 401, 403, 404, 500, 501})
    void withServerError(final int code) throws Exception {
        mockMvc.perform(get("/httpServerErrorException/" + code))
                .andDo(print())
                .andExpect(status()
                        .is(code))
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/badCredentialsException"
    })
    void statusUnauthorized(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isUnauthorized())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/accessDeniedException"
    })
    void statusForbidden(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isForbidden())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/illegalArgumentException",
            "/illegalStateException",
            "/numberFormatException"
    })
    void statusBadRequest(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
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
    void statusNotFound(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isNotFound())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/dataIntegrityViolationException",
            "/concurrencyFailureException",
            "/entityExistsException",
            "/optimisticLockException",
            "/pessimisticLockException"
    })
    void statusConflict(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isConflict())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/etagDoesntMatchException"
    })
    void statusPreconditionFailed(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isPreconditionFailed())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/badSqlGrammarException",
            "/unsupportedEncodingException",
            "/unsupportedOperationException"
    })
    void statusNotImplemented(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isNotImplemented())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/nullPointerException"
    })
    void statusInternalServerError(final String url) throws Exception {
        mockMvc.perform(get(url)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("GET " + url))
                .andDo(print())
                .andExpect(status()
                        .isInternalServerError())
                .andExpect(header()
                        .exists(HttpHeaders.CACHE_CONTROL))
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status")
                        .exists())
                .andExpect(jsonPath("$.detail")
                        .exists());
    }
}
