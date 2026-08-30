package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Ally;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AllyResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static AllyResponse fromEntity(Ally ally) {
        AllyResponse response = new AllyResponse();
        response.setCensusId(ally.getCensusId());
        response.setName(ally.getName());
        return response;
    }
}
