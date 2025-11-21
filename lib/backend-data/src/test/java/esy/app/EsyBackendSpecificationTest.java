package esy.app;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
public class EsyBackendSpecificationTest {

    static <T> void assertApiFor(final Class<T> clazz) {
        final var root = "../".repeat(2);
        final var path = Paths.get(root, "doc", "api", "%sRestApi.adoc".formatted(clazz.getSimpleName()));
        assertTrue(Files.exists(path), path.toString());
    }

    @Test
    void restApi() {
        assertApiFor(Enum.class);
        assertApiFor(Owner.class);
        assertApiFor(Pet.class);
        assertApiFor(Vet.class);
        assertApiFor(Visit.class);
    }
}
