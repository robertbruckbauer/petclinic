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
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(PetGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class PetGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private PetRepository petRepository;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @Test
    void queryAllPet() {
        final var owner = Owner.fromJson("""
                {
                    "name":"Alice"
                }
                """);
        final var value = Pet.fromJson("""
                {
                    "name":"Tom",
                    "born":"2021-04-22",
                    "species":"Cat"
                }
                """)
                .setOwner(owner);
        when(petRepository.findAll())
                .thenReturn(List.of(value));
        when(ownerRepository.findAllById(any()))
                .thenReturn(List.of(owner));
        final var data = graphQlTester
                .document("""
                        {allPet{name owner{name}}}
                        """)
                .execute();
        assertNotNull(data);
        data.path("allPet[0].name")
                .hasValue()
                .entity(String.class)
                .isEqualTo("Tom");
        data.path("allPet[0].owner.name")
                .hasValue()
                .entity(String.class)
                .isEqualTo("Alice");
        verify(petRepository).findAll();
        verifyNoMoreInteractions(petRepository);
        verify(ownerRepository).findAllById(any());
        verifyNoMoreInteractions(ownerRepository);
    }
}
