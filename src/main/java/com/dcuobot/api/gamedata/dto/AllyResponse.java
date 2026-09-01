package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Ally;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "An equippable ally item.")
public class AllyResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census item id, matched against an equipped ally's id.")
    private String censusId;

    @Schema(description = "Ally name.")
    private String name;

    public static AllyResponse fromEntity(Ally ally) {
        AllyResponse response = new AllyResponse();
        response.setCensusId(ally.getCensusId());
        response.setName(ally.getName());
        return response;
    }
}
