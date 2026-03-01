package esy.app.clinic;

import esy.api.clinic.Vet;
import esy.app.EsyGraphqlConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
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
                """).addAllSkill("Z", "A").addAllSpecies("Dog", "Cat");
        when(vetRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allVet{name allSkill allSpecies}}
                        """)
                .execute();
        assertNotNull(data);
        final var allName = data.path("allVet[*].name")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(1, allName.size());
        assertEquals("Tom", allName.getFirst());
        final var allSkill = data.path("allVet[0].allSkill")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(2, allSkill.size());
        assertEquals("A", allSkill.get(0));
        assertEquals("Z", allSkill.get(1));
        final var allSpecies = data.path("allVet[0].allSpecies")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(2, allSpecies.size());
        assertEquals("Cat", allSpecies.get(0));
        assertEquals("Dog", allSpecies.get(1));
        verify(vetRepository).findAll();
        verifyNoMoreInteractions(vetRepository);
    }
}
