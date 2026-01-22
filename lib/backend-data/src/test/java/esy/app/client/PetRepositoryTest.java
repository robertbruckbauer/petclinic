package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.Pet;
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
public class PetRepositoryTest {

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

    @Autowired
    private PetRepository petRepository;

    @Test
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(ownerRepository);
        assertNotNull(petRepository);
    }

    Pet createWithName(final String name) {
        return Pet.parseJson("""
            {
                "name":"%s",
                "born":"2021-04-22",
                "species":"Cat"
            }
            """.formatted(name));
    }

    Owner saveOwner(final String name) {
        return ownerRepository.save(Owner.parseJson("""
            {
                "name":"%s",
                "address":"Bergweg 1, 5400 Hallein",
                "contact":"+43 660 5557683"
            }
            """.formatted(name)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Käthe",
            "Jörg",
            "Ümit"
    })
    void savePet(final String name) {
        final var value0 = createWithName(name);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());
        assertNotNull(value0.getId());
        assertEquals(name, value0.getName());

        final var owner = saveOwner("Max Mustermann");
        final var value1 = petRepository.save(value0.setOwner(owner));
        assertNotNull(value1);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));
    }

    @Test
    void savePetWithSameNameDifferentOwners() {
        final var owner1 = saveOwner("Max Mustermann");
        final var owner2 = saveOwner("Mia Musterfrau");
        final var name = "Garfield";
        final var pet1 = petRepository.save(createWithName(name).setOwner(owner1));
        assertNotNull(pet1);
        final var pet2 = petRepository.save(createWithName(name).setOwner(owner2));
        assertNotNull(pet2);
        assertEquals(name, pet1.getName());
        assertEquals(name, pet2.getName());
        assertNotEquals(pet1.getId(), pet2.getId());
        assertNotEquals(pet1.getOwner().getId(), pet2.getOwner().getId());
    }

    @Test
    void savePetWithSameNameSameOwnerFails() {
        final var owner = saveOwner("Max Mustermann");
        final var name = "Garfield";
        final var pet1 = petRepository.save(createWithName(name).setOwner(owner));
        assertNotNull(pet1);
        final var pet2 = createWithName(name).setOwner(owner);
        assertThrows(Exception.class, () -> {
            petRepository.save(pet2);
            petRepository.flush();
        });
    }

    @Test
    void findPet() {
        final var owner = saveOwner("Max Mustermann");
        final var name = "Odi";
        final var value = petRepository.save(createWithName(name).setOwner(owner));
        assertTrue(petRepository.existsById(value.getId()));
        assertTrue(petRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findPetNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(petRepository.existsById(uuid));
        assertFalse(petRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var owner = saveOwner("Max Mustermann");
        final var value1 = petRepository.save(createWithName("Odi").setOwner(owner));
        final var value2 = petRepository.save(createWithName("Tom").setOwner(owner));
        final var value3 = petRepository.save(createWithName("Jerry").setOwner(owner));
        assertEquals(3, petRepository.count());
        final var allValue = petRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
