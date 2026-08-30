package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CensusCharacterGenderList {
    @JsonProperty("character_list")
    private Collection<CensusCharacterGender> characterList;

    private int returned;
}
