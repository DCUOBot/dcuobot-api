package com.dcuobot.api.guild.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuildCharacterResponse {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("world_id")
    private String worldId;

    private int rank;

    private String name;

    @JsonProperty("skill_points")
    private int skillPoints;

    @JsonProperty("combat_rating")
    private int combatRating;

    @JsonProperty("pvp_combat_rating")
    private int pvpCombatRating;
}
