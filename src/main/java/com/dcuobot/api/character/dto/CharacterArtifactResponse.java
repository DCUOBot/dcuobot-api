package com.dcuobot.api.character.dto;

import com.dcuobot.api.gamedata.entity.Artifact;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CharacterArtifactResponse {
    private String id;

    private String name;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("discord_emoji_id")
    private String discordEmojiId;

    public static CharacterArtifactResponse fromEntity(Artifact artifact) {
        CharacterArtifactResponse response = new CharacterArtifactResponse();
        response.setId(artifact.getCensusId());
        response.setName(artifact.getName());
        response.setImageUrl(artifact.getImageUrl());
        response.setDiscordEmojiId(artifact.getDiscordEmojiId());
        return response;
    }
}
