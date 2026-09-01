package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character gender.")
public class GenderResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census gender id, matched against a character's gender field.")
    private String censusId;

    @Schema(description = "Gender name.")
    private String name;

    @JsonProperty("image_url")
    @Schema(description = "URL of the placeholder paperdoll image used when a character of this " +
            "gender has no rendered paperdoll yet.")
    private String imageUrl;

    public static GenderResponse fromEntity(Gender gender) {
        GenderResponse response = new GenderResponse();
        response.setCensusId(gender.getCensusId());
        response.setName(gender.getName());
        response.setImageUrl(gender.getImageUrl());
        return response;
    }
}
