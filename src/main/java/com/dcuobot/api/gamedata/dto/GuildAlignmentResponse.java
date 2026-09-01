package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.GuildAlignment;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A guild (league) alignment, e.g. Hero, Villain, Vigilante.")
public class GuildAlignmentResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census alignment id, matched against a guild's alignment field.")
    private String censusId;

    @Schema(description = "Alignment name.")
    private String name;

    public static GuildAlignmentResponse fromEntity(GuildAlignment guildAlignment) {
        GuildAlignmentResponse response = new GuildAlignmentResponse();
        response.setCensusId(guildAlignment.getCensusId());
        response.setName(guildAlignment.getName());
        return response;
    }
}
