package com.dcuobot.api.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CharacterResponse {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("world_id")
    private String worldId;

    private String name;

    private String alignment;

    private String gender;

    @JsonProperty("power_type")
    private String powerType;

    @JsonProperty("movement_mode")
    private String movementMode;

    private String personality;

    @JsonProperty("combat_rating")
    private int combatRating;

    @JsonProperty("pvp_combat_rating")
    private int pvpCombatRating;

    private CharacterStatsResponse stats;

    @JsonProperty("skill_points")
    private int skillPoints;

    private CharacterGuildResponse guild;

    private Collection<CharacterArtifactResponse> artifacts;

    private Collection<CharacterAllyResponse> allies;

    private CharacterImageResponse image;
}
