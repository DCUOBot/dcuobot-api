package com.dcuobot.api.character.dto;

import lombok.Data;

@Data
public class CharacterStatsResponse {
    private int health;

    private int power;

    private int defense;

    private int toughness;

    private int might;

    private int precision;

    private int restoration;

    private int vitalization;

    private int dominance;
}
