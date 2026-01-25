package esy.app.client;

import esy.api.client.Owner;
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
public class OwnerRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(ownerRepository);
    }

    Owner createWithName(final String name) {
        return Owner.fromJson("""
            {
                "name":"%s",
                "address":"Bergweg 1, 5400 Hallein",
                "contact":"+43 660 5557683"
            }
            """.formatted(name));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Jörg Weiß",
            "Bärbel Krüger"
    })
    void saveOwner(final String name) {
        final var value0 = createWithName(name);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(name, value0.getName());
        assertFalse(value0.getAddress().isBlank());
        assertFalse(value0.getContact().isBlank());

        final var value1 = ownerRepository.save(value0);
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Test
    void findOwner() {
        final var name = "Max Mustermann";
        final var value = ownerRepository.save(createWithName(name));
        assertTrue(ownerRepository.existsById(value.getId()));
        assertTrue(ownerRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findOwnerNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(ownerRepository.existsById(uuid));
        assertFalse(ownerRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = ownerRepository.save(createWithName("Max Mustermann"));
        final var value2 = ownerRepository.save(createWithName("Mia Musterfrau"));
        final var value3 = ownerRepository.save(createWithName("Jet Li"));
        assertEquals(3, ownerRepository.count());
        final var allValue = ownerRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
