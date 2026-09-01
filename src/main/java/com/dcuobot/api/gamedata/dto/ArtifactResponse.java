package com.dcuobot.api.gamedata.dto;

import com.dcuobot.api.gamedata.entity.Artifact;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "An equippable artifact item.")
public class ArtifactResponse {
    @JsonProperty("census_id")
    @Schema(description = "Census item id, matched against an equipped artifact's id.")
    private String censusId;

    @Schema(description = "Artifact name.")
    private String name;

    @JsonProperty("image_url")
    @Schema(description = "URL of the artifact's icon image.")
    private String imageUrl;

    @JsonProperty("discord_emoji_id")
    @Schema(description = "Id of the Discord emoji used to represent this artifact in the DCUOBot " +
            "Discord bot.")
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
