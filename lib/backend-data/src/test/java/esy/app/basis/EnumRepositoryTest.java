package esy.app.basis;

import esy.api.basis.Enum;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@Transactional
@Rollback(true)
public class EnumRepositoryTest {

    static final String ENUM_ART = "TEST";

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
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(enumRepository);
    }

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

    @ParameterizedTest
    @ValueSource(strings = {
            "Äpfel",
            "Öl",
            "Übel",
            "Spaß"
    })
    void saveEnum(final String name) {
        final var code = enumRepository.count(ENUM_ART);
        final var value0 = createWithName(name, code);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertEquals(ENUM_ART, value0.getArt());
        assertEquals(code, value0.getCode());
        assertEquals(name, value0.getName());
        assertEquals("A " + name, value0.getText());

        final var value1 = enumRepository.save(value0);
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void saveEnumUniqueKeyConstraint() {
        final var code = 1L;
        final var name = "JIRA";
        final var value = enumRepository.save(createWithName(name, code));
        try {
            assertThrows(DataIntegrityViolationException.class, () ->
                    transactionTemplate.execute(status ->
                            enumRepository.save(createWithName(name, 8L))));
            assertThrows(DataIntegrityViolationException.class, () ->
                    transactionTemplate.execute(status ->
                            enumRepository.save(createWithName("ARIJ", code))));
        } finally {
            enumRepository.delete(value);
        }
    }

    @Test
    void findEnum() {
        final var code = 1L;
        final var name = "JIRA";
        final var value = enumRepository.save(createWithName(name, code));
        assertEquals(value, enumRepository.findByCode(ENUM_ART, code).orElseThrow());
        assertEquals(value, enumRepository.findByName(ENUM_ART, name).orElseThrow());
        assertTrue(enumRepository.existsById(value.getId()));
        assertTrue(enumRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findEnumNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(enumRepository.existsById(uuid));
        assertFalse(enumRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = enumRepository.save(createWithName("JIRA", 1L));
        final var value2 = enumRepository.save(createWithName("WIKI", 2L));
        final var value3 = enumRepository.save(createWithName("TEST", 3L));
        assertEquals(3, enumRepository.count(ENUM_ART));
        final var allValue = enumRepository.findAll(ENUM_ART);
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
