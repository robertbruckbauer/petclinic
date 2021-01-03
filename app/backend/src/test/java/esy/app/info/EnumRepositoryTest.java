package esy.app.info;

import esy.api.info.Enum;
import esy.app.DatabaseConfiguration;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
@DataJpaTest
@ContextConfiguration(classes = DatabaseConfiguration.class)
@Transactional
@Rollback(false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EnumRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private EnumRepository enumRepository;

    @Test
    @Order(0)
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(enumRepository);
    }

    static final String ENUM_ART = "TEST";

    Enum createWithName(final String name, final Long code) {
        final String json = "{" +
                "\"art\": \"" + ENUM_ART + "\"," +
                "\"code\": \"" + code + "\","+
                "\"name\": \"" + name + "\"," +
                "\"text\": \"A " + name + "\"" +
                "}";
        return Enum.parseJson(json);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "JIRA",
            "JIRA Cloud",
            "TEST",
            "Äpfel",
            "Öl",
            "Übel",
            "Spaß"
    })
    @Order(1)
    void saveEnum(final String name) {
        final Long code = enumRepository.count(ENUM_ART);
        final Enum value0 = createWithName(name, code);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertEquals(ENUM_ART, value0.getArt());
        assertEquals(code, value0.getCode());
        assertEquals(name, value0.getName());
        assertEquals("A " + name, value0.getText());

        final Enum value1 = transactionTemplate.execute(status ->
                enumRepository.save(value0));
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    @Order(2)
    void saveEnumUniqueKeyConstraint() {
        assertEquals(7, enumRepository.count(ENUM_ART));
        assertThrows(DataIntegrityViolationException.class, () ->
                transactionTemplate.execute(status ->
                        enumRepository.save(createWithName("JIRA", 8L))));
        assertThrows(DataIntegrityViolationException.class, () ->
                transactionTemplate.execute(status ->
                        enumRepository.save(createWithName("ARIJ", 1L))));
    }

    @Test
    @Order(3)
    void findEnum() {
        assertEquals(7, enumRepository.count(ENUM_ART));
        final Enum value = enumRepository.findAll(ENUM_ART).get(0);
        assertEquals(value, enumRepository.findByCode(ENUM_ART, 0L).orElseThrow());
        assertEquals(value, enumRepository.findByName(ENUM_ART, "JIRA").orElseThrow());
        assertTrue(enumRepository.existsById(value.getId()));
        assertTrue(enumRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    @Order(4)
    void findEnumNoElement() {
        final UUID uuid = UUID.randomUUID();
        assertFalse(enumRepository.existsById(uuid));
        assertFalse(enumRepository.findById(uuid).isPresent());
    }

    @Test
    @Order(5)
    void findAll() {
        assertEquals(7, enumRepository.count(ENUM_ART));
        final List<Enum> allValue = enumRepository.findAll(ENUM_ART);
        assertEquals(7, allValue.size());
        assertEquals("JIRA", allValue.get(0).getName());
        assertEquals("JIRA Cloud", allValue.get(1).getName());
        assertEquals("TEST", allValue.get(2).getName());
        assertEquals("Äpfel", allValue.get(3).getName());
        assertEquals("Öl", allValue.get(4).getName());
        assertEquals("Übel", allValue.get(5).getName());
        assertEquals("Spaß", allValue.get(6).getName());
        assertTrue(allValue.removeAll(enumRepository.findAll()));
        assertTrue(allValue.isEmpty());
    }

    @Test
    @Order(99)
    void deleteAll() {
        assertEquals(7, enumRepository.count(ENUM_ART));
        enumRepository.findAll(ENUM_ART).forEach(e ->
                enumRepository.delete(e));
        assertEquals(0, enumRepository.count(ENUM_ART));
    }
}
