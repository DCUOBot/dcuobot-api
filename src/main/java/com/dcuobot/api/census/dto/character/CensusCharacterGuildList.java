package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CensusCharacterGuildList {
    @JsonProperty("guild_list")
    private Collection<CensusCharacterGuild> guildList;

    private int returned;
}
