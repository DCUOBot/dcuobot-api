package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusCharacterGender {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("gender_id")
    private String genderId;
}
