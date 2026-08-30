package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusCharacterItemList {
    @JsonProperty("characters_item_list")
    private CensusCharacterItem[] charactersItemList;

    private int returned;
}
