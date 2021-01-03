package esy.app.info;

import esy.api.info.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.InputMismatchException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("fast")
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@WebMvcTest({VersionRestController.class})
class VersionRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VersionRepository versionRepository;

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
    void getVersion() throws Exception {
        when(versionRepository.find())
                .thenReturn(Version.fromString(new Scanner("2.1")));
        mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.major")
                        .value("2"))
                .andExpect(jsonPath("$.minor")
                        .value("1"));
    }

    @Test
    void getVersionAdoc() throws Exception {
        when(versionRepository.find())
                .thenReturn(Version.fromString(new Scanner("2.1")));
        mockMvc.perform(get("/version.adoc")
                .accept("text/asciidoc"))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("text/asciidoc;charset=UTF-8"))
                .andExpect(content()
                        .string(equalTo("2.1\n")));
    }

    @Test
    void getVersionHtml() throws Exception {
        when(versionRepository.find())
                .thenReturn(Version.fromString(new Scanner("2.1")));
        mockMvc.perform(get("/version.html")
                .accept("text/html"))
                .andDo(print())
                .andExpect(status()
                        .isOk())
                .andExpect(content()
                        .contentType("text/html;charset=UTF-8"))
                .andExpect(content()
                        .string(equalTo("<span id=\"version\">2.1</span><br/>\n")));
    }

    @Test
    void getVersionOnMissingResourceException() throws Exception {
        when(versionRepository.find())
                .thenThrow(mock(MissingResourceException.class));
        mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    void getVersionOnNoSuchElementException() throws Exception {
        when(versionRepository.find())
                .thenThrow(mock(NoSuchElementException.class));
        mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest());
    }

    @Test
    void getVersionOnInputMismatchException() throws Exception {
        when(versionRepository.find())
                .thenThrow(mock(InputMismatchException.class));
        mockMvc.perform(get("/version")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status()
                        .isBadRequest());
    }
}
