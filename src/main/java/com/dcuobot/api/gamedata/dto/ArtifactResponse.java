package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Artifact;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ArtifactResponse {
    @JsonProperty("census_id")
    private String censusId;

    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("discord_emoji_id")
    private String discordEmojiId;

    public static ArtifactResponse fromEntity(Artifact artifact) {
        ArtifactResponse response = new ArtifactResponse();
        response.setCensusId(artifact.getCensusId());
        response.setName(artifact.getName());
        response.setImageUrl(artifact.getImageUrl());
        response.setDiscordEmojiId(artifact.getDiscordEmojiId());
        return response;
    }
}
