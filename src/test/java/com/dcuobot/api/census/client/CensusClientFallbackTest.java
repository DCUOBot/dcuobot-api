package com.dcuobot.api.census.client;

import com.dcuobot.api.census.exception.CensusException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Verifies every {@link CensusClient} method - both the abstract ones implemented directly by
 * {@link CensusClientFallback}, and the default ones that delegate to them - surfaces the same
 * {@link CensusException} once the circuit breaker routes here, rather than some methods failing
 * differently than others.
 */
class CensusClientFallbackTest {
    private final CensusClientFallback fallback = new CensusClientFallback();

    @Test
    void getCharacter_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class)
                .isThrownBy(() -> fallback.getCharacter("n", "c", "w", "j", "s", "l", "e", "d", "cr"));
    }

    @Test
    void getCharacterByNameAndWorldId_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class)
                .isThrownBy(() -> fallback.getCharacter("Batman", "1000", false));
    }

    @Test
    void getCharacterGuild_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(() -> fallback.getCharacterGuild("guild-1"));
    }

    @Test
    void getCharacterArtifacts_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(() -> fallback.getCharacterArtifacts("char-1"));
    }

    @Test
    void getCharacterAllies_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(() -> fallback.getCharacterAllies("char-1"));
    }

    @Test
    void getCharacterGender_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(() -> fallback.getCharacterGender("char-1"));
    }

    @Test
    void getCharacterRanking_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class)
                .isThrownBy(() -> fallback.getCharacterRanking("1000", "combat_rating"));
    }

    @Test
    void getGuild_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class)
                .isThrownBy(() -> fallback.getGuild("Justice League", "1000"));
    }

    @Test
    void getGuildRoster_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(() -> fallback.getGuildRoster("guild-1"));
    }

    @Test
    void getGameServerStatus_throwsCensusException() {
        assertThatExceptionOfType(CensusException.class).isThrownBy(fallback::getGameServerStatus);
    }
}
