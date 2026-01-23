package esy.app.basis;

import esy.api.basis.Ping;
import esy.app.EsyBackendConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@ContextConfiguration(classes = EsyBackendConfiguration.class)
@Transactional
@Rollback(true)
public class PingRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PingRepository pingRepository;

    @Test
    void context() {
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
        assertNotNull(entityManager);
        assertNotNull(transactionTemplate);
        assertNotNull(pingRepository);
    }

    Ping createWithTime(final LocalDateTime time) {
        return Ping.fromJson("""
                {
                    "at":"%s"
                }
                """.formatted(Ping.TIME_FORMATTER.format(time)));
    }

    static Stream<LocalDateTime> atSource() {
        return Stream.of(
                LocalDateTime.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(1, 3, 5, 123456789)),
                LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.now()),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0),
                LocalDateTime.of(2000, 12, 31, 23, 59, 59, 999999999));
    }

    @ParameterizedTest
    @MethodSource("atSource")
    void saveEnum(final LocalDateTime at) {
        final var value0 = createWithTime(at);
        assertFalse(value0.isPersisted());
        assertEquals(0L, value0.getVersion());

        final var value1 = pingRepository.saveAndFlush(value0);
        assertNotNull(value1);
        assertNotSame(value1, value0);
        assertTrue(value1.isPersisted());
        assertEquals(0L, value1.getVersion());
        assertTrue(value1.isEqual(value0));

        final var value2 = pingRepository.saveAndFlush(value0.touch());
        assertNotNull(value2);
        assertNotSame(value2, value0);
        assertTrue(value2.isPersisted());
        assertEquals(1L, value2.getVersion());
        assertTrue(value2.isEqual(value0));
    }

    @Test
    void findPing() {
        final var value = pingRepository.save(createWithTime(LocalDateTime.now()));
        assertTrue(pingRepository.existsById(value.getId()));
        assertTrue(pingRepository.findById(value.getId()).orElseThrow().isEqual(value));
    }

    @Test
    void findPingNoElement() {
        final var uuid = UUID.randomUUID();
        assertFalse(pingRepository.existsById(uuid));
        assertFalse(pingRepository.findById(uuid).isPresent());
    }

    @Test
    void findAll() {
        final var value1 = pingRepository.save(createWithTime(LocalDateTime.now()));
        final var value2 = pingRepository.save(createWithTime(LocalDateTime.now()));
        final var value3 = pingRepository.save(createWithTime(LocalDateTime.now()));
        assertEquals(3, pingRepository.count());
        final var allValue = pingRepository.findAll();
        assertEquals(3, allValue.size());
        assertTrue(allValue.remove(value1));
        assertTrue(allValue.remove(value2));
        assertTrue(allValue.remove(value3));
        assertTrue(allValue.isEmpty());
    }
}
