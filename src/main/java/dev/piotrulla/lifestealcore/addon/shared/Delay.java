package dev.piotrulla.lifestealcore.addon.shared;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class Delay {

    private final Cache<@NotNull UUID, @NotNull Instant> delays;

    private final Duration defaultDelay;

    public Delay(Duration defaultDelay) {
        if (defaultDelay.isZero() || defaultDelay.isNegative()) {
            throw new IllegalArgumentException("defaultDelay must be positive and non-zero");
        }
        this.defaultDelay = defaultDelay;

        this.delays = Caffeine.newBuilder()
                .expireAfterWrite(defaultDelay)
                .expireAfterAccess(defaultDelay)
                .build();
    }

    public Delay(int defaultDelay) {
        this(Duration.ofSeconds(defaultDelay));
    }

    public void markDelay(UUID key, Duration delay) {
        this.delays.put(key, Instant.now().plus(delay));
    }

    public void markDelay(UUID key) {
        this.markDelay(key, this.defaultDelay);
    }

    public void unmarkDelay(UUID key) {
        this.delays.invalidate(key);
    }

    public boolean hasDelay(UUID key) {
        Instant delayExpireMoment = this.getDelayExpireMoment(key);

        return Instant.now().isBefore(delayExpireMoment);
    }

    public Duration getDurationToExpire(UUID key) {
        return Duration.between(Instant.now(), this.getDelayExpireMoment(key));
    }

    private Instant getDelayExpireMoment(UUID key) {
        return this.delays.asMap().getOrDefault(key, Instant.MIN);
    }

}
