package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GenderResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    public static GenderResponse fromEntity(Gender gender) {
        GenderResponse response = new GenderResponse();
        response.setCensusId(gender.getCensusId());
        response.setName(gender.getName());
        response.setImageUrl(gender.getImageUrl());
        return response;
    }
}
