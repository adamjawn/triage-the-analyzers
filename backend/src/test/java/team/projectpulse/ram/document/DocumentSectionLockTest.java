package team.projectpulse.ram.document;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins isLocked's three states (unlocked, manual lock with no expiry, timed lock) so that simplifying its
 * boolean logic for PMD SimplifyBooleanReturns cannot change what it returns.
 */
class DocumentSectionLockTest {

    private static final Instant NOW = Instant.parse("2026-11-13T12:00:00Z");

    @Test
    void anUnlockedSectionIsNotLocked() {
        assertThat(new DocumentSectionLock().isLocked(NOW)).isFalse();
    }

    @Test
    void aManualLockWithNoExpiryIsLocked() {
        DocumentSectionLock lock = new DocumentSectionLock();
        lock.lock(null, NOW.minusSeconds(3600), null, "manual");
        assertThat(lock.isLocked(NOW)).isTrue();
    }

    @Test
    void aTimedLockIsLockedUntilItExpires() {
        DocumentSectionLock lock = new DocumentSectionLock();
        lock.lock(null, NOW.minusSeconds(60), NOW.plusSeconds(60), null);
        assertThat(lock.isLocked(NOW)).isTrue();
        assertThat(lock.isLocked(NOW.plusSeconds(60))).isFalse();
        assertThat(lock.isLocked(NOW.plusSeconds(61))).isFalse();
    }

    @Test
    void anUnlockedSectionIgnoresAStaleExpiry() {
        DocumentSectionLock lock = new DocumentSectionLock();
        lock.setExpiresAt(NOW.plusSeconds(60));
        assertThat(lock.isLocked(NOW)).isFalse();
    }

}
