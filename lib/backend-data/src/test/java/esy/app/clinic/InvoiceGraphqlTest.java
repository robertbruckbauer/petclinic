package esy.app.clinic;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import esy.api.clinic.Invoice;
import esy.app.EsyGraphqlConfiguration;
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
@GraphQlTest(InvoiceGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class InvoiceGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private InvoiceRepository invoiceRepository;

    Invoice createWithText(final String text) {
        return Invoice.fromJson("""
                {
                    "at":"2024-01-15",
                    "due":"2024-02-15",
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
    }

    @Test
    void queryAllInvoiceAt() {
        final var at = "2024-01-15";
        final var text = "Lorem tempus.";
        final var value = Invoice.fromJson("""
                {
                    "at":"%s",
                    "due":"2024-02-15",
                    "status":"D",
                    "text":"%s"
                }
                """.formatted(at, text));
        when(invoiceRepository.findAll(any(BooleanExpression.class), any(OrderSpecifier.class)))
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allInvoiceAt(at: "%s"){text}}
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
        assertEquals("invoice.at = " + at,
                queryCaptor.getValue().toString());
        assertEquals("invoice.at ASC",
                orderCaptor.getValue().toString());
    }
}
