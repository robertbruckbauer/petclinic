package esy.api.basis;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("fast")
class SexTest {

    @Test
    void text() {
        assertEquals("male", Sex.M.text());
        assertEquals("female", Sex.F.text());
    }
}
