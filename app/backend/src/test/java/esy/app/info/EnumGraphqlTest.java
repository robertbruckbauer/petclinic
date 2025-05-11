package esy.app.info;

import esy.api.info.Enum;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Tag("fast")
@GraphQlTest(EnumGraphqlController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({MockitoExtension.class})
class EnumGraphqlTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockitoBean
    private EnumRepository enumRepository;

    @Test
    @Order(1)
    void queryAllEnum() {
        final var art = "species";
        final var value1 = Enum.parseJson("{" +
                "\"code\": 0," +
                "\"name\":\"Cat\"," +
                "\"text\":\"A cat (tax. felis catus) is a domestic species of a small carnivorous mammal.\"" +
                "}");
        final var value2 = Enum.parseJson("{" +
                "\"code\": 1," +
                "\"name\":\"Dog\"," +
                "\"text\":\"A dog (tax. canis familiaris) is a domesticated descendant of the wolf.\"" +
                "}");
        when(enumRepository.findAll(art))
                .thenReturn(List.of(value1, value2));
        final var query = String.format("{allEnum(art:\"%s\"){name}}", art);
        final var data = graphQlTester
                .document(query)
                .execute();
        assertNotNull(data);
        final var allName = data.path("allEnum[*].name")
                .hasValue()
                .entityList(String.class)
                .get();
        assertEquals(2, allName.size());
        assertEquals("Cat", allName.get(0));
        assertEquals("Dog", allName.get(1));
        verify(enumRepository).findAll(art);
        verifyNoMoreInteractions(enumRepository);
    }
}
