package com.dcuobot.api.census.dto.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusGuildRosterCharacter {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("world_id")
    private String worldId;

    @JsonProperty("database_id")
    private String databaseId;

    private String name;

    @JsonProperty("alignment_id")
    private String alignmentId;

    @JsonProperty("gender_id")
    private String genderId;

    @JsonProperty("power_type_id")
    private String powerTypeId;

    @JsonProperty("power_source_id")
    private String powerSourceId;

    @JsonProperty("movement_mode_id")
    private String movementModeId;

    @JsonProperty("region_id")
    private String regionId;

    private String level;

    @JsonProperty("origin_id")
    private String originId;

    @JsonProperty("personality_id")
    private String personalityId;

    private String active;

    @JsonProperty("current_health")
    private String currentHealth;

    @JsonProperty("current_power")
    private String currentPower;

    @JsonProperty("skill_points")
    private String skillPoints;

    @JsonProperty("combat_rating")
    private String combatRating;

    @JsonProperty("pvp_combat_rating")
    private String pvpCombatRating;

    private String deleted;

    private String hash;
}
