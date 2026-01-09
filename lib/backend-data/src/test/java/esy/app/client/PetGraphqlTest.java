package esy.app.client;

import esy.api.basis.Sex;
import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.app.EsyGraphqlConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
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

    static Pet createWithName(final String name) {
        return Pet.fromJson("""
                {
                    "name":"%s",
                    "born":"2021-04-22",
                    "species":"Cat",
                    "sex":"M"
                }
                """.formatted(name));
    }

    static Owner createOwner(final String name) {
        return Owner.fromJson("""
                {
                    "name":"%s"
                }
                """.formatted(name));
    }

    @Test
    void queryAllPet() {
        final var value = createWithName("Tom")
                .setOwner(createOwner("Alice Cooper"));
        when(petRepository.findAll())
                .thenReturn(List.of(value));
        when(ownerRepository.findAllById(anySet()))
                .thenReturn(List.of(value.getOwner()));
        final var data = graphQlTester
                .document("""
                        {allPet{name sex owner{name}}}
                        """)
                .execute();
        assertNotNull(data);
        data.path("allPet[0].name")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getName());
        data.path("allPet[0].sex")
                .hasValue()
                .entity(Sex.class)
                .isEqualTo(value.getSex());
        data.path("allPet[0].owner.name")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getOwner().getName());
        verify(petRepository).findAll();
        verifyNoMoreInteractions(petRepository);
        verify(ownerRepository).findAllById(anySet());
        verifyNoMoreInteractions(ownerRepository);
    }
}
