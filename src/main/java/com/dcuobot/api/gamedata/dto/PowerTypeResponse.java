package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.PowerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character power type, e.g. Fire, Ice, Gadgets, Light.")
public class PowerTypeResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census power type id, matched against a character's power type field.")
    private String censusId;

    @Schema(description = "Power type name.")
    private String name;

    public static PowerTypeResponse fromEntity(PowerType powerType) {
        PowerTypeResponse response = new PowerTypeResponse();
        response.setCensusId(powerType.getCensusId());
        response.setName(powerType.getName());
        return response;
    }
}
