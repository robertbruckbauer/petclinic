package esy.app.clinic;

import esy.api.clinic.Visit;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(VisitGraphqlController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class})
class VisitGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VisitRepository visitRepository;

    @Test
    @Order(1)
    void queryAllVisit() {
        final var value = Visit.parseJson("{" +
                "\"date\":\"2021-04-22\"," +
                "\"text\":\"Lorem ipsum.\"" +
                "}");
        when(visitRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("{allVisit{text}}")
                .execute();
        assertNotNull(data);
        final var allText = data.path("allVisit[*].text")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allText.size());
        assertEquals("Lorem ipsum.", allText.getFirst());
        verify(visitRepository).findAll();
        verifyNoMoreInteractions(visitRepository);
    }
}
