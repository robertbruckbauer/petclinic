package esy.api.clinic;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("fast")
class InvoiceStatusTest {

    @Test
    void text() {
        assertEquals("drafted", InvoiceStatus.D.text());
        assertEquals("issued", InvoiceStatus.I.text());
        assertEquals("completed", InvoiceStatus.C.text());
        assertEquals("cancelled", InvoiceStatus.X.text());
    }
}
