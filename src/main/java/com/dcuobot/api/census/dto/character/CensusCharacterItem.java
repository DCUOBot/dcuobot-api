package com.dcuobot.api.census.dto.character;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusCharacterItem {
    @JsonProperty("character_id")
    private String characterId;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("equipment_slot_id")
    private String equipmentSlotId;
}
