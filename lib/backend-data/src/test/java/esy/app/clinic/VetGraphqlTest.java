package esy.app.clinic;

import esy.api.clinic.Vet;
import esy.app.EsyGraphqlConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
@GraphQlTest(VetGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class VetGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private VetRepository vetRepository;

    static Vet createWithName(final String name) {
        return Vet.fromJson("""
                {
                	"name":"%s",
                	"allSkill":["Z","A"],
                	"allSpecies":["Dog","Cat"]
                }
                """.formatted(name));
    }

    @Test
    void queryAllVet() {
        final var value = createWithName("Tom");
        when(vetRepository.findAll())
                .thenReturn(List.of(value));
        final var data = graphQlTester
                .document("""
                        {allVet{name allSkill allSpecies}}
                        """)
                .execute();
        assertNotNull(data);
        data.path("allVet[0].name")
                .hasValue()
                .entity(String.class)
                .isEqualTo(value.getName());
        data.path("allVet[0].allSkill")
                .hasValue()
                .entityList(String.class)
                .containsExactly("A", "Z");
        data.path("allVet[0].allSpecies")
                .hasValue()
                .entityList(String.class)
                .containsExactly("Cat", "Dog");
        verify(vetRepository).findAll();
        verifyNoMoreInteractions(vetRepository);
    }
}
