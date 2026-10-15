package esy.app.clinic;

import esy.api.clinic.Invoice;
import esy.api.clinic.InvoiceStatus;
import esy.api.clinic.Visit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@Transactional
@Rollback(true)
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
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

    Visit saveVisit(final String date) {
        return visitRepository.save(Visit.fromJson("""
                {
                    "date":"%s",
                    "text":"Treatment"
                }
                """.formatted(date)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Tschüss und schöne Grüße!"
    })
    void saveInvoice(final String text) {
        final var visit = saveVisit("2024-01-10");
        final var value0 = createWithText(text).setVisit(visit);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(text, value0.getText());
        assertNotNull(value0.getIssued());
        assertNotNull(value0.getPeriod());
        assertEquals(InvoiceStatus.D, value0.getStatus());
        assertNotNull(value0.getVisit());

        final var value1 = invoiceRepository.save(value0);
        assertNotNull(value1);
        assertNotSame(value0, value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertNotNull(value1.getVisit());
        assertTrue(value1.isEqual(value0));
    }

    @Test
    void findInvoice() {
        final var text = "Lorem ipsum";
        final var visit = saveVisit("2024-01-11");
        final var value = invoiceRepository.save(createWithText(text).setVisit(visit));
        assertTrue(invoiceRepository.existsById(value.getId()));
        assertTrue(invoiceRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findInvoiceNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(invoiceRepository.existsById(uuid));
        assertFalse(invoiceRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = invoiceRepository.save(createWithText("Lorem ipsum").setVisit(saveVisit("2024-02-01")));
        final var value2 = invoiceRepository.save(createWithText("Dolor sit amet").setVisit(saveVisit("2024-02-02")));
        final var value3 = invoiceRepository.save(createWithText("Dolore magna aliqua").setVisit(saveVisit("2024-02-03")));
        assertEquals(3, invoiceRepository.count());
        final var allValue = invoiceRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
