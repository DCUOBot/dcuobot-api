package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.MovementMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character movement mode, e.g. Flight, Acrobatics, Super Speed.")
public class MovementModeResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census movement mode id, matched against a character's movement mode field.")
    private String censusId;

    @Schema(description = "Movement mode name.")
    private String name;

    public static MovementModeResponse fromEntity(MovementMode movementMode) {
        MovementModeResponse response = new MovementModeResponse();
        response.setCensusId(movementMode.getCensusId());
        response.setName(movementMode.getName());
        return response;
    }
}
