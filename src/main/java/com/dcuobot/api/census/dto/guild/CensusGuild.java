package com.dcuobot.api.census.dto.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusGuild {
    @JsonProperty("world_id")
    private String worldId;

    @JsonProperty("guild_id")
    private String guildId;

    private String name;

    @JsonProperty("character_alignment_id")
    private String characterAlignmentId;
}
