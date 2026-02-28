package esy.app.clinic;

import esy.api.clinic.Vet;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
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
public class VetRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private VetRepository vetRepository;

    @Test
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(vetRepository);
    }

    Vet createWithName(final String name) {
        return Vet.fromJson("""
            {
                "name":"%s"
            }
            """.formatted(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Jörg Weiß",
            "Bärbel Krüger"
    })
    void saveVet(final String name) {
        final var value0 = createWithName(name).addAllSkill("Z", "A");
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(name, value0.getName());
        assertEquals(2, value0.getAllSkill().size());
        assertEquals("A", value0.getAllSkill().first());
        assertEquals("Z", value0.getAllSkill().last());

        final var value1 = vetRepository.save(value0);
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
        assertEquals(2, value1.getAllSkill().size());
        assertEquals("A", value1.getAllSkill().first());
        assertEquals("Z", value1.getAllSkill().last());
    }

    @Test
    void findVet() {
        final var name = "Max Mustermann";
        final var value = vetRepository.save(createWithName(name));
        assertTrue(vetRepository.existsById(value.getId()));
    }

    @Test
    void findVetNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(vetRepository.existsById(uuid));
        assertFalse(vetRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = vetRepository.save(createWithName("Max Mustermann"));
        final var value2 = vetRepository.save(createWithName("Mia Musterfrau"));
        final var value3 = vetRepository.save(createWithName("Jet Li"));
        assertEquals(3, vetRepository.count());
        final var allValue = vetRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
