package esy.app.basis;

import esy.api.basis.Enum;
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
@GraphQlTest(EnumGraphqlController.class)
@Import(EsyGraphqlConfiguration.class)
@ExtendWith({MockitoExtension.class})
class EnumGraphqlTest {

    static final String ENUM_ART = "TEST";

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private EnumRepository enumRepository;

    Enum createWithName(final String name, final Long code) {
        final var json = """
                {
                    "art":"%s",
                    "code":"%d",
                    "name":"%3$s",
                    "text":"A %3$s"
                }
                """.formatted(ENUM_ART, code, name);
        return Enum.fromJson(json);
    }

    @Test
    void queryAllEnum() {
        final var value1 = createWithName("Cat", 0L);
        final var value2 = createWithName("Dog", 1L);
        when(enumRepository.findAll(ENUM_ART))
                .thenReturn(List.of(value1, value2));
        final var data = graphQlTester
                .document("""
                        {allEnum(art:"%s"){name}}
                        """.formatted(ENUM_ART))
                .execute();
        assertNotNull(data);
        data.path("allEnum[*].name")
                .hasValue()
                .entityList(String.class)
                .containsExactly("Cat", "Dog");
        verify(enumRepository).findAll(ENUM_ART);
        verifyNoMoreInteractions(enumRepository);
    }
}
