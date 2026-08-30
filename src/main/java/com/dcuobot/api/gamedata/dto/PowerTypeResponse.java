package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.PowerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PowerTypeResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static PowerTypeResponse fromEntity(PowerType powerType) {
        PowerTypeResponse response = new PowerTypeResponse();
        response.setCensusId(powerType.getCensusId());
        response.setName(powerType.getName());
        return response;
    }
}
