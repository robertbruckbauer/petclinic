package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.app.EsyGraphqlConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(OwnerGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class OwnerGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @MockitoBean
    private PetRepository petRepository;

    static Owner createWithName(final String name) {
        return Owner.fromJson("""
            {
                "name":"%s",
                "address":"Bergweg 1, 5400 Hallein",
                "contact":"+43 660 5557683"
            }
            """.formatted(name));
    }

    @Test
    void queryAllOwner() {
        final var value = createWithName("Jet Li");
        final var pet = Pet.fromJson("""
                {
                    "name":"Tom"
                }
                """)
                .setOwner(value);
        when(ownerRepository.findAll())
                .thenReturn(List.of(value));
        when(petRepository.findAllByOwnerIdIn(anySet()))
                .thenReturn(List.of(pet));
        final var data = graphQlTester
                .document("""
                        {allOwner{name allPet{name}}}
                        """)
                .execute();
        assertNotNull(data);
        data.path("allOwner[0].name")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getName());
        data.path("allOwner[0].allPet[0].name")
                .hasValue()
                .entity(String.class)
                .isEqualTo(pet.getName());
        verify(ownerRepository).findAll();
        verifyNoMoreInteractions(ownerRepository);
        verify(petRepository).findAllByOwnerIdIn(anySet());
        verifyNoMoreInteractions(petRepository);
    }
}
