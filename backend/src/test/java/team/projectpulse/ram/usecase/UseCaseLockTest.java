package team.projectpulse.ram.usecase;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins isLocked's three states (unlocked, manual lock with no expiry, timed lock) so that simplifying its
 * boolean logic for PMD SimplifyBooleanReturns cannot change what it returns.
 */
class UseCaseLockTest {

    private static final Instant NOW = Instant.parse("2026-11-13T12:00:00Z");

    @Test
    void anUnlockedUseCaseIsNotLocked() {
        assertThat(new UseCaseLock().isLocked(NOW)).isFalse();
    }

    @Test
    void aManualLockWithNoExpiryIsLocked() {
        UseCaseLock lock = new UseCaseLock();
        lock.lock(null, NOW.minusSeconds(3600), null, "manual");
        assertThat(lock.isLocked(NOW)).isTrue();
    }

    @Test
    void aTimedLockIsLockedUntilItExpires() {
        UseCaseLock lock = new UseCaseLock();
        lock.lock(null, NOW.minusSeconds(60), NOW.plusSeconds(60), null);
        assertThat(lock.isLocked(NOW)).isTrue();
        assertThat(lock.isLocked(NOW.plusSeconds(60))).isFalse();
        assertThat(lock.isLocked(NOW.plusSeconds(61))).isFalse();
    }

}
