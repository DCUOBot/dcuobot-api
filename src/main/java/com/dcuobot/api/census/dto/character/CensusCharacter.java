package com.dcuobot.api.census.dto.character;

import com.dcuobot.api.census.dto.guild.CensusGuildRoster;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CensusCharacter {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("world_id")
    private String worldId;

    private String name;

    @JsonProperty("alignment_id")
    private String alignmentId;

    @JsonProperty("gender_id")
    private String genderId;

    @JsonProperty("power_type_id")
    private String powerTypeId;

    @JsonProperty("movement_mode_id")
    private String movementModeId;

    @JsonProperty("personality_id")
    private String personalityId;

    @JsonProperty("combat_rating")
    private String combatRating;

    @JsonProperty("pvp_combat_rating")
    private String pvpCombatRating;

    @JsonProperty("max_health")
    private String maxHealth;

    @JsonProperty("max_power")
    private String maxPower;

    private String defense;

    private String toughness;

    private String might;

    private String precision;

    private String restoration;

    private String vitalization;

    private String dominance;

    @JsonProperty("skill_points")
    private String skillPoints;

    @JsonProperty("character_id_join_guild_roster")
    private List<CensusGuildRoster> guildRoster;
}
