package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Alignment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlignmentResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static AlignmentResponse fromEntity(Alignment alignment) {
        AlignmentResponse response = new AlignmentResponse();
        response.setCensusId(alignment.getCensusId());
        response.setName(alignment.getName());
        return response;
    }
}
