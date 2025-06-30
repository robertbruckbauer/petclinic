package esy.app.clinic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import esy.api.clinic.Visit;
import esy.app.BackendConfiguration;
import esy.app.GraphqlConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(VisitGraphqlController.class)
@Import(GraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class VisitGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VisitRepository visitRepository;

    @Test
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

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-04-01",
            "2021-04-22",
            "2021-04-30"
    })
    void queryAllVisitAt(final String date) {
        final var value = Visit.parseJson("{" +
                "\"date\":\"" + date + "\"," +
                "\"text\":\"Lorem ipsum.\"" +
                "}");
        when(visitRepository.findAll(any(BooleanExpression.class), any(OrderSpecifier.class)))
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("{allVisitAt(date: \"" + date + "\"){text}}")
                .execute();
        assertNotNull(data);
        final var allText = data.path("allVisitAt[*].text")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allText.size());
        assertEquals("Lorem ipsum.", allText.getFirst());
        final var queryCaptor = ArgumentCaptor.<BooleanExpression>captor();
        final var orderCaptor = ArgumentCaptor.<OrderSpecifier<LocalDate>>captor();
        verify(visitRepository).findAll(queryCaptor.capture(), orderCaptor.capture());
        verifyNoMoreInteractions(visitRepository);
        assertEquals("visit.date = " + date,
                queryCaptor.getValue().toString());
        assertEquals("visit.date ASC",
                orderCaptor.getValue().toString());
    }
}
