package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusCharacterGuild {
    @JsonProperty("world_id")
    private String worldId;

    @JsonProperty("guild_id")
    private String guildId;

    private String name;

    @JsonProperty("lower_name")
    private String lowerName;

    @JsonProperty("character_alignment_id")
    private String characterAlignmentId;
}
