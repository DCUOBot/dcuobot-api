package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CensusCharacterList {
    @JsonProperty("character_list")
    private Collection<CensusCharacter> characterList;

    private int returned;
}
