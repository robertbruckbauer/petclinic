package esy.app.clinic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.app.EsyGraphqlConfiguration;
import esy.app.client.PetRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(VisitGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class VisitGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VisitRepository visitRepository;

    @MockitoBean
    private PetRepository petRepository;

    @MockitoBean
    private VetRepository vetRepository;

    Visit createWithText(final String text) {
        return Visit.fromJson("""
            {
                "date":"2021-04-22",
                "time":"13:27",
                "text":"%s"
            }
            """.formatted(text));
    }

    @Test
    void queryAllVisit() {
        final var value = createWithText("Lorem ipsum.");
        when(visitRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("{allVisit{text billable}}")
                .execute();
        assertNotNull(data);
        data.path("allVisit[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getText());
        data.path("allVisit[0].billable")
                .hasValue()
                .entity(Boolean.class)
                .isEqualTo(value.isBillable());
        verify(visitRepository).findAll();
        verifyNoMoreInteractions(visitRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(vetRepository);
    }

    @Test
    void queryAllVisitWithPetAndVet() {
        final var pet = Pet.fromJson("""
                {
                    "name":"Tom",
                    "born":"2021-04-22",
                    "species":"Cat"
                }""");
        final var vet = Vet.fromJson("""
                {
                    "name":"Dr. Smith"
                }""");
        final var text = "Ipso facto.";
        final var value = createWithText(text)
                .setPet(pet)
                .setVet(vet);
        when(visitRepository.findAll())
                .thenReturn(List.of(value));
        when(petRepository.findAllById(any()))
                .thenReturn(List.of(pet));
        when(vetRepository.findAllById(any()))
                .thenReturn(List.of(vet));
        final var data = graphQlTester
                .document("{allVisit{text pet{name} vet{name}}}")
                .execute();
        assertNotNull(data);
        data.path("allVisit[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(text);
        data.path("allVisit[0].pet.name")
                .hasValue()
                .entity(String.class)
                .isEqualTo("Tom");
        data.path("allVisit[0].vet.name")
                .hasValue()
                .entity(String.class)
                .isEqualTo("Dr. Smith");
        verify(visitRepository).findAll();
        verifyNoMoreInteractions(visitRepository);
        verify(petRepository).findAllById(any());
        verifyNoMoreInteractions(petRepository);
        verify(vetRepository).findAllById(any());
        verifyNoMoreInteractions(vetRepository);
    }

    @Test
    void queryAllVisitAt() {
        final var date = "2021-04-22";
        final var text = "Lorem tempus.";
        final var value = Visit.fromJson("""
                {
                    "date":"%s",
                    "text":"%s"
                }
                """.formatted(date, text));
        when(visitRepository.findAll(any(BooleanExpression.class), any(OrderSpecifier.class)))
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allVisitAt(date: "%s"){text}}
                        """.formatted(date))
                .execute();
        assertNotNull(data);
        data.path("allVisitAt[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(text);
        final var queryCaptor = ArgumentCaptor.<BooleanExpression>captor();
        final var orderCaptor = ArgumentCaptor.<OrderSpecifier<LocalDate>>captor();
        verify(visitRepository).findAll(queryCaptor.capture(), orderCaptor.capture());
        verifyNoMoreInteractions(visitRepository);
        verifyNoInteractions(petRepository);
        verifyNoInteractions(vetRepository);
        assertEquals("visit.date = " + date,
                queryCaptor.getValue().toString());
        assertEquals("visit.date ASC",
                orderCaptor.getValue().toString());
    }
}
