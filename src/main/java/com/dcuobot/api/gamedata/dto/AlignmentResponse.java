package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Alignment;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character alignment, e.g. Hero or Villain.")
public class AlignmentResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census alignment id, matched against a character's alignment field.")
    private String censusId;

    @Schema(description = "Alignment name.")
    private String name;

    public static AlignmentResponse fromEntity(Alignment alignment) {
        AlignmentResponse response = new AlignmentResponse();
        response.setCensusId(alignment.getCensusId());
        response.setName(alignment.getName());
        return response;
    }
}
