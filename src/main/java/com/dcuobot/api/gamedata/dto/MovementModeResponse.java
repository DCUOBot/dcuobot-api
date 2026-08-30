package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.MovementMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MovementModeResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static MovementModeResponse fromEntity(MovementMode movementMode) {
        MovementModeResponse response = new MovementModeResponse();
        response.setCensusId(movementMode.getCensusId());
        response.setName(movementMode.getName());
        return response;
    }
}
