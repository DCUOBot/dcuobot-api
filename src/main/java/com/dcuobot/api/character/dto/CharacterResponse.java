package com.dcuobot.api.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;

@Data
@Schema(description = "A DC Universe Online character.")
public class CharacterResponse {
    @JsonProperty("character_id")
    @Schema(description = "Census character id.")
    private String characterId;

    @JsonProperty("world_id")
    @Schema(description = "World (server) id the character belongs to.", example = "2")
    private String worldId;

    @Schema(description = "Character name.")
    private String name;

    @Schema(description = "Character alignment, e.g. Hero or Villain. Null in ranking mode.")
    private String alignment;

    @Schema(description = "Character gender, either \"Male\" or \"Female\".")
    private String gender;

    @JsonProperty("power_type")
    @Schema(description = "Character's power type, e.g. Fire, Ice, Gadgets. Null in ranking mode.")
    private String powerType;

    @JsonProperty("movement_mode")
    @Schema(description = "Character's movement mode, e.g. Flight, Acrobatics. Null in ranking mode.")
    private String movementMode;

    @Schema(description = "Character's roleplay personality trait. Null in ranking mode.")
    private String personality;

    @JsonProperty("combat_rating")
    @Schema(description = "Character's PvE combat rating.")
    private int combatRating;

    @JsonProperty("pvp_combat_rating")
    @Schema(description = "Character's PvP combat rating.")
    private int pvpCombatRating;

    @Schema(description = "Character's core stats (health, power, might, precision, etc.).")
    private CharacterStatsResponse stats;

    @JsonProperty("skill_points")
    @Schema(description = "Total skill points earned.")
    private int skillPoints;

    @Schema(description = "Guild (league) the character belongs to. Null if the character is not in a " +
            "guild, or if returned in ranking mode.")
    private CharacterGuildResponse guild;

    @Schema(description = "Artifacts currently equipped. Null in ranking mode.")
    private Collection<CharacterArtifactResponse> artifacts;

    @Schema(description = "Allies currently equipped. Null in ranking mode.")
    private Collection<CharacterAllyResponse> allies;

    @Schema(description = "The character's rendered paperdoll image, with a placeholder fallback URL.")
    private CharacterImageResponse image;
}
