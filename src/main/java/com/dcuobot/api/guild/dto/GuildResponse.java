package com.dcuobot.api.guild.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GuildResponse {
    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("world_id")
    private String worldId;

    private String name;

    private String alignment;

    @JsonProperty("member_count")
    private int memberCount;

    @JsonProperty("average_skill_points")
    private double averageSkillPoints;

    @JsonProperty("average_combat_rating")
    private double averageCombatRating;

    @JsonProperty("average_pvp_combat_rating")
    private double averagePvpCombatRating;

    private List<GuildCharacterResponse> characters;
}
