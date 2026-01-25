package esy.app.clinic;

import esy.api.clinic.Vet;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(VetGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class VetGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VetRepository vetRepository;

    @Test
    void queryAllVet() {
        final var value = Vet.fromJson("""
                {
                    "name":"Tom"
                }
                """);
        when(vetRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allVet{name}}
                        """)
                .execute();
        assertNotNull(data);
        final var allName = data.path("allVet[*].name")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allName.size());
        assertEquals("Tom", allName.getFirst());
        verify(vetRepository).findAll();
        verifyNoMoreInteractions(vetRepository);
    }
}
