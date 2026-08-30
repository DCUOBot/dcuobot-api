package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.GuildAlignment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuildAlignmentResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    public static GuildAlignmentResponse fromEntity(GuildAlignment guildAlignment) {
        GuildAlignmentResponse response = new GuildAlignmentResponse();
        response.setCensusId(guildAlignment.getCensusId());
        response.setName(guildAlignment.getName());
        return response;
    }
}
