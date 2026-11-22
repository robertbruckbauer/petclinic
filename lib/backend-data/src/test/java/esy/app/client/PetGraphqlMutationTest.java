package esy.app.client;

import esy.api.client.QOwner;
import esy.api.client.QPet;
import esy.app.DatabaseCleaner;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@AutoConfigureGraphQlTester
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PetGraphqlMutationTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    private static final String MUTATION = """
            mutation($ownerName: String!, $petSpecies: String!) {
                createOwnerWithPet(
                    owner: {name: $ownerName, address: "Bergweg 1"}
                    pet: {name: "Tom", born: "2021-04-22", species: $petSpecies, sex: M}
                ) {
                    id
                    name
                    owner { id name }
                }
            }
            """;

    @Test
    @Order(1)
    void mutationCreateOwnerWithPet() {
        final var ownerName = "Max Mustermann";
        graphQlTester.document(MUTATION)
                .variable("ownerName", ownerName)
                .variable("petSpecies", "Cat")
                .execute()
                .errors().verify()
                .path("createOwnerWithPet.name").entity(String.class).isEqualTo("Tom")
                .path("createOwnerWithPet.owner.name").entity(String.class).isEqualTo(ownerName);
        final var owner = ownerRepository.findOne(QOwner.owner.name.eq(ownerName)).orElseThrow();
        assertEquals("", owner.getContact());
        assertTrue(petRepository.findOne(QPet.pet.owner.id.eq(owner.getId())).isPresent());
    }

    @Test
    @Order(2)
    void mutationCreateOwnerWithPetRollback() {
        final var ownerName = "Erika Mustermann";
        graphQlTester.document(MUTATION)
                .variable("ownerName", ownerName)
                .variable("petSpecies", "x".repeat(513)) // pet.species is VARCHAR(512)
                .execute()
                .errors()
                .satisfy(errors -> assertFalse(errors.isEmpty()));
        assertFalse(ownerRepository.findOne(QOwner.owner.name.eq(ownerName)).isPresent());
    }

    @Test
    @Order(999)
    void cleanup() {
        assertDoesNotThrow(databaseCleaner::cleanDatabase);
    }
}
