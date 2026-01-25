package esy.app.basis;

import esy.api.basis.Ping;
import lombok.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Tag("slow")
@SpringBootTest
@Transactional
@Rollback(true)
class PingSchedulerTest {

    Ping createWithTime(@NonNull final LocalDateTime time) {
        return Ping.fromJson("""
            {
                "at":"%s"
            }
            """.formatted(Ping.TIME_FORMATTER.format(time)));
    }

    @Autowired
    Clock clock;

    @Autowired
    PingRepository pingRepository;

    @Autowired
    PingScheduler pingScheduler;

    @Test
    void scheduleNoElement() {
        assertThat(pingRepository.count()).isEqualTo(0);
        assertDoesNotThrow(() -> pingScheduler.run());
        assertThat(pingRepository.count()).isEqualTo(0);
    }

    @Test
    void scheduleNoMatch() {
        final var at = LocalDateTime.now(clock).plusHours(168).plusSeconds(1);
        final var value = pingRepository.save(createWithTime(at));
        assertNotNull(value);
        assertEquals(at, value.getAt());
        assertThat(pingRepository.count()).isEqualTo(1);
        assertDoesNotThrow(() -> pingScheduler.run());
        assertThat(pingRepository.count()).isEqualTo(1);
    }

    @Test
    void scheduleMatch() {
        final var at = LocalDateTime.now(clock).minusHours(168).minusSeconds(1);
        final var value = pingRepository.save(createWithTime(at));
        assertNotNull(value);
        assertEquals(at, value.getAt());
        assertThat(pingRepository.count()).isEqualTo(1);
        assertDoesNotThrow(() -> pingScheduler.run());
        assertThat(pingRepository.count()).isEqualTo(0);
    }
}