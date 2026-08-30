package esy.app.clinic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import esy.api.clinic.Invoice;
import esy.api.clinic.Visit;
import esy.app.EsyGraphqlConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(InvoiceGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class InvoiceGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private InvoiceRepository invoiceRepository;

    @MockitoBean
    private VisitRepository visitRepository;

    Invoice createWithText(final String text) {
        return Invoice.fromJson("""
                {
                    "issued":"2024-01-15",
                    "period":"PT720H",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(text));
    }

    @Test
    void queryAllInvoice() {
        final var value = createWithText("Lorem ipsum.");
        when(invoiceRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("{allInvoice{text status}}")
                .execute();
        assertNotNull(data);
        data.path("allInvoice[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getText());
        data.path("allInvoice[0].status")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getStatus().name());
        verify(invoiceRepository).findAll();
        verifyNoMoreInteractions(invoiceRepository);
        verifyNoInteractions(visitRepository);
    }

    @Test
    void queryAllInvoiceWithVisit() {
        final var visit = Visit.fromJson("""
                {
                    "date":"2024-01-10",
                    "text":"Treatment"
                }""");
        final var text = "Ipso facto.";
        final var value = createWithText(text).setVisit(visit);
        when(invoiceRepository.findAll())
                .thenReturn(List.of(value));
        when(visitRepository.findAllById(any()))
                .thenReturn(List.of(visit));
        final var data = graphQlTester
                .document("{allInvoice{text visit{date}}}")
                .execute();
        assertNotNull(data);
        data.path("allInvoice[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(text);
        data.path("allInvoice[0].visit.date")
                .hasValue()
                .entity(String.class)
                .isEqualTo("2024-01-10");
        verify(invoiceRepository).findAll();
        verifyNoMoreInteractions(invoiceRepository);
        verify(visitRepository).findAllById(any());
        verifyNoMoreInteractions(visitRepository);
    }

    @Test
    void queryAllInvoiceAt() {
        final var at = "2024-01-15";
        final var text = "Lorem tempus.";
        final var value = Invoice.fromJson("""
                {
                    "issued":"%s",
                    "period":"PT720H",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(at, text));
        when(invoiceRepository.findAll(any(BooleanExpression.class), any(OrderSpecifier.class)))
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allInvoiceAt(issued: "%s"){text}}
                        """.formatted(at))
                .execute();
        assertNotNull(data);
        data.path("allInvoiceAt[0].text")
                .hasValue()
                .entity(String.class)
                .isEqualTo(text);
        final var queryCaptor = ArgumentCaptor.<BooleanExpression>captor();
        final var orderCaptor = ArgumentCaptor.<OrderSpecifier<LocalDate>>captor();
        verify(invoiceRepository).findAll(queryCaptor.capture(), orderCaptor.capture());
        verifyNoMoreInteractions(invoiceRepository);
        verifyNoInteractions(visitRepository);
        assertEquals("invoice.issued = " + at,
                queryCaptor.getValue().toString());
        assertEquals("invoice.issued ASC",
                orderCaptor.getValue().toString());
    }
}
