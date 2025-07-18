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

    private <T> Path restApiFor(final Class<T> clazz) {
        return Paths.get("src/main/java/%sRestApi.adoc".formatted(
                clazz.getName()
                        .replace("esy.api.", "esy/app/")
                        .replaceAll("\\.", "/")));
    }

    @Test
    void restApi() {
        assertTrue(Files.exists(restApiFor(Enum.class)));
        assertTrue(Files.exists(restApiFor(Owner.class)));
        assertTrue(Files.exists(restApiFor(Pet.class)));
        assertTrue(Files.exists(restApiFor(Vet.class)));
        assertTrue(Files.exists(restApiFor(Visit.class)));
    }
}
