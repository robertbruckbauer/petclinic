package esy.app.clinic;

import esy.api.clinic.Invoice;
import esy.api.clinic.InvoiceStatus;
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

    @ParameterizedTest
    @ValueSource(strings = {
            "Tschüss und schöne Grüße!"
    })
    void saveInvoice(final String text) {
        final var value0 = createWithText(text);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(text, value0.getText());
        assertNotNull(value0.getAt());
        assertNotNull(value0.getDue());
        assertEquals(InvoiceStatus.D, value0.getStatus());

        final var value1 = invoiceRepository.save(value0);
        assertNotNull(value1);
        assertNotSame(value0, value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Test
    void findInvoice() {
        final var text = "Lorem ipsum";
        final var value = invoiceRepository.save(createWithText(text));
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
        final var value1 = invoiceRepository.save(createWithText("Lorem ipsum"));
        final var value2 = invoiceRepository.save(createWithText("Dolor sit amet"));
        final var value3 = invoiceRepository.save(createWithText("Dolore magna aliqua"));
        assertEquals(3, invoiceRepository.count());
        final var allValue = invoiceRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
