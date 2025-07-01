package esy.app.client;

import esy.api.client.Owner;
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
@GraphQlTest(OwnerGraphqlController.class)
@Import(GraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class OwnerGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private OwnerRepository ownerRepository;

    @MockitoBean
    private PetRepository petRepository;

    @Test
    void queryAllOwner() {
        final var value = Owner.parseJson("""
                {
                    "name":"Tom",
                    "address":"Bergweg 1, 5400 Hallein",
                    "contact":"+43 660 5557683"
                }
                """);
        when(ownerRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allOwner{name}}
                        """)
                .execute();
        assertNotNull(data);
        final var allName = data.path("allOwner[*].name")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allName.size());
        assertEquals("Tom", allName.getFirst());
        verify(ownerRepository).findAll();
        verifyNoMoreInteractions(ownerRepository);
        verifyNoInteractions(petRepository);
    }
}
