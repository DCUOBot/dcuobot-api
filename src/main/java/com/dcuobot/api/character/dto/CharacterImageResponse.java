package com.dcuobot.api.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A character's paperdoll image, with a placeholder fallback.")
public class CharacterImageResponse {
    @Schema(description = "URL of this API's character image endpoint " +
            "(GET /v1/census/characters/{characterId}/image), which serves the rendered paperdoll or " +
            "falls back to altUrl if none is available yet.")
    private String url;

    @JsonProperty("alt_url")
    @Schema(description = "URL of the gender-based placeholder image used as a fallback when no " +
            "rendered paperdoll is available.")
    private String altUrl;
}
