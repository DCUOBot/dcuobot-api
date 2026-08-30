package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Personality;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PersonalityResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static PersonalityResponse fromEntity(Personality personality) {
        PersonalityResponse response = new PersonalityResponse();
        response.setCensusId(personality.getCensusId());
        response.setName(personality.getName());
        return response;
    }
}
