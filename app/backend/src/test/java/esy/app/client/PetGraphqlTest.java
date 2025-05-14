package esy.app.client;

import esy.api.client.Pet;
import esy.app.GraphqlConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(PetGraphqlController.class)
@Import(GraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class PetGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private PetRepository petRepository;

    @Test
    void queryAllPet() {
        final var value = Pet.parseJson("{" +
                "\"name\":\"Tom\"," +
                "\"born\":\"2021-04-22\"," +
                "\"species\":\"Cat\"" +
                "}");
        when(petRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("{allPet{name}}")
                .execute();
        assertNotNull(data);
        final var allName = data.path("allPet[*].name")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allName.size());
        assertEquals("Tom", allName.getFirst());
        verify(petRepository).findAll();
        verifyNoMoreInteractions(petRepository);
    }
}
