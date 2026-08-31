package com.dcuobot.api.census.dto.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusGuildRoster {
    @JsonProperty("world_id")
    private String worldId;

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("character_id")
    private String characterId;

    private String rank;

    @JsonProperty("character_id_join_character")
    private CensusGuildRosterCharacter guildRosterCharacter;
}
