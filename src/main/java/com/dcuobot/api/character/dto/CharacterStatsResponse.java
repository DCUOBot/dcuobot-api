package com.dcuobot.api.character.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character's core stats.")
public class CharacterStatsResponse {
    @Schema(description = "Maximum health.")
    private int health;

    @Schema(description = "Maximum power.")
    private int power;

    @Schema(description = "Defense stat.")
    private int defense;

    @Schema(description = "Toughness stat.")
    private int toughness;

    @Schema(description = "Might stat, driving damage for might-based powers.")
    private int might;

    @Schema(description = "Precision stat, driving damage for precision-based (weapon) attacks.")
    private int precision;

    @Schema(description = "Restoration stat, driving healing power.")
    private int restoration;

    @Schema(description = "Vitalization stat, driving power regeneration for allies/group.")
    private int vitalization;

    @Schema(description = "Dominance stat, driving crowd-control effectiveness for tanks.")
    private int dominance;
}
