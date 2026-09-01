package com.dcuobot.api.guild.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A member of a guild's roster.")
public class GuildCharacterResponse {
    @JsonProperty("character_id")
    @Schema(description = "Census character id.")
    private String characterId;

    @JsonProperty("world_id")
    @Schema(description = "World (server) id the character belongs to.", example = "2")
    private String worldId;

    @Schema(description = "Guild rank, where 0 is the leader and higher numbers are lower ranks.")
    private int rank;

    @Schema(description = "Character name.")
    private String name;

    @JsonProperty("skill_points")
    @Schema(description = "Total skill points earned.")
    private int skillPoints;

    @JsonProperty("combat_rating")
    @Schema(description = "Character's PvE combat rating.")
    private int combatRating;

    @JsonProperty("pvp_combat_rating")
    @Schema(description = "Character's PvP combat rating.")
    private int pvpCombatRating;
}
