package com.dcuobot.api.character.dto;

import com.dcuobot.api.gamedata.entity.Artifact;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "An artifact equipped by a character.")
public class CharacterArtifactResponse {
    @Schema(description = "Census artifact item id.")
    private String id;

    @Schema(description = "Artifact name.")
    private String name;

    @JsonProperty("image_url")
    @Schema(description = "URL of the artifact's icon image.")
    private String imageUrl;

    @JsonProperty("discord_emoji_id")
    @Schema(description = "Id of the Discord emoji used to represent this artifact in the DCUOBot " +
            "Discord bot.")
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
