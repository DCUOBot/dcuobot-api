package com.dcuobot.api.census.client;

import com.dcuobot.api.census.dto.character.CensusCharacterGenderList;
import com.dcuobot.api.census.dto.character.CensusCharacterGuildList;
import com.dcuobot.api.census.dto.character.CensusCharacterItemList;
import com.dcuobot.api.census.dto.character.CensusCharacterList;
import com.dcuobot.api.census.dto.guild.CensusGuildList;
import com.dcuobot.api.census.dto.guild.CensusGuildRosterList;
import com.dcuobot.api.census.dto.status.CensusGameServerStatusList;
import com.dcuobot.api.census.exception.CensusException;
import org.springframework.stereotype.Component;

/**
 * Used in place of {@link CensusClient} whenever its circuit breaker is open, or a call to it
 * times out or fails outright, so every failure mode collapses to the same
 * {@link CensusException} ("did not respond") that callers already handle, rather than leaking a
 * raw Feign/resilience4j exception up to {@link com.dcuobot.api.common.exception.GlobalExceptionHandler}.
 */
@Component
public class CensusClientFallback implements CensusClient {
    @Override
    public CensusCharacterList getCharacter(String name, String cCase, String worldId, String cJoin, String cShow,
                                            String cLimit, String cExactMatchFirst, String deleted,
                                            String combatRating) {
        throw new CensusException();
    }

    @Override
    public CensusCharacterGuildList getCharacterGuild(String guildId) {
        throw new CensusException();
    }

    @Override
    public CensusCharacterItemList getCharacterItems(String characterId, String equipmentSlotId, String cSort,
                                                      String cLimit, String cShow) {
        throw new CensusException();
    }

    @Override
    public CensusCharacterGenderList getCharacterGender(String characterId, String cShow) {
        throw new CensusException();
    }

    @Override
    public CensusCharacterList getCharacterRanking(String worldId, String cSort, String level, String cLimit,
                                                    String skillPoints, String deleted, String cShow) {
        throw new CensusException();
    }

    @Override
    public CensusGuildList getGuild(String name, String cCase, String worldId, String cExactMatchFirst,
                                    String cLimit, String cShow) {
        throw new CensusException();
    }

    @Override
    public CensusGuildRosterList getGuildRoster(String cLimit, String cSort, String cJoin, String on,
                                                String guildId) {
        throw new CensusException();
    }

    @Override
    public CensusGameServerStatusList getGameServerStatus(String cLimit, String gameCode, String cSort) {
        throw new CensusException();
    }
}
