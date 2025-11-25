package esy.app;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("fast")
public class EsyBackendSpecificationTest {

    static <T> void assertRestApiFor(final Class<T> clazz) {
        final var root = "../".repeat(2);
        final var path = Paths.get(root, "doc", "restapi", "%sRestApi.adoc".formatted(clazz.getSimpleName()));
        assertTrue(Files.exists(path), path.toString());
    }

    @Test
    void restApi() {
        assertRestApiFor(Enum.class);
        assertRestApiFor(Owner.class);
        assertRestApiFor(Pet.class);
        assertRestApiFor(Vet.class);
        assertRestApiFor(Visit.class);
    }

    static <T> void assertGraphQLFor(final Class<T> clazz) {
        final var root = "../".repeat(2);
        final var path = Paths.get(root, "doc", "graphql", "%sGraphQL.adoc".formatted(clazz.getSimpleName()));
        assertTrue(Files.exists(path), path.toString());
    }

    @Test
    void graphQL() {
        //assertGraphQLFor(Enum.class);
        assertGraphQLFor(Owner.class);
        //assertGraphQLFor(Pet.class);
        //assertGraphQLFor(Vet.class);
        //assertGraphQLFor(Visit.class);
    }
}
