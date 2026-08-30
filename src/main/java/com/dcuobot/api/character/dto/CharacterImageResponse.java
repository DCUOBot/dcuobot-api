package com.dcuobot.api.character.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CharacterImageResponse {
    private String url;

    @JsonProperty("alt_url")
    private String altUrl;
}
