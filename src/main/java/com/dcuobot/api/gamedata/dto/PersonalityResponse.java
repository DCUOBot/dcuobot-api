package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Personality;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character's roleplay personality trait.")
public class PersonalityResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census personality id, matched against a character's personality field.")
    private String censusId;

    @Schema(description = "Personality name.")
    private String name;

    public static PersonalityResponse fromEntity(Personality personality) {
        PersonalityResponse response = new PersonalityResponse();
        response.setCensusId(personality.getCensusId());
        response.setName(personality.getName());
        return response;
    }
}
