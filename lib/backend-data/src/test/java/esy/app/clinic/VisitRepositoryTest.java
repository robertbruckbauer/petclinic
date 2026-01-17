package esy.app.clinic;

import esy.api.clinic.Visit;
import esy.app.EsyBackendConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@ContextConfiguration(classes = EsyBackendConfiguration.class)
@Transactional
@Rollback(true)
public class VisitRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(visitRepository);
    }

    Visit createWithText(final String text) {
        return Visit.parseJson("""
            {
                "date":"2021-04-22",
                "time":"13:27",
                "text":"%s"
            }
            """.formatted(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Tschüss und schöne Grüße!"
    })
    void saveVisit(final String text) {
        final var value0 = createWithText(text);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(text, value0.getText());

        final var value1 = visitRepository.save(value0);
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Test
    void findVet() {
        final var text = "Lorem ipsum";
        final var value = visitRepository.save(createWithText(text));
        assertTrue(visitRepository.existsById(value.getId()));
        assertTrue(visitRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findVetNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(visitRepository.existsById(uuid));
        assertFalse(visitRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = visitRepository.save(createWithText("Lorem ipsue"));
        final var value2 = visitRepository.save(createWithText("Dolor sit amet"));
        final var value3 = visitRepository.save(createWithText("Dolore magna aliqua"));
        assertEquals(3, visitRepository.count());
        final var allValue = visitRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
