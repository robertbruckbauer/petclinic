package esy.app.basis;

import esy.api.basis.QPing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class PingScheduler {

    private final Clock clock;

    private final PingRepository pingRepository;

    @Value("${schedule.ping.max-age}")
    private Duration maxAge = Duration.ZERO;

    @Scheduled(initialDelay = 15L, fixedDelay = 180L, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void run() {
        final var query = QPing.ping.at.before(LocalDateTime.now(clock).minus(maxAge));
        pingRepository.deleteAll(pingRepository.findAll(query));
    }
}
