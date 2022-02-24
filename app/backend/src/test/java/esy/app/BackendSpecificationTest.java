package esy.app;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import esy.api.info.Version;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
public class BackendSpecificationTest {

    private <T> Path restApiFor(final Class<T> clazz) {
        return Paths.get(String.format("src/main/java/%sRestApi.adoc",
                clazz.getName()
                        .replace("esy.api.", "esy/app/")
                        .replaceAll("\\.", "/")));
    }

    @Test
    void service() {
        // check existence of all adoc files for doc/service.adoc
        assertTrue(Files.exists(restApiFor(Enum.class)));
        assertTrue(Files.exists(restApiFor(Version.class)));
        assertTrue(Files.exists(restApiFor(Owner.class)));
        assertTrue(Files.exists(restApiFor(Pet.class)));
        assertTrue(Files.exists(restApiFor(Vet.class)));
        assertTrue(Files.exists(restApiFor(Visit.class)));
    }
}
