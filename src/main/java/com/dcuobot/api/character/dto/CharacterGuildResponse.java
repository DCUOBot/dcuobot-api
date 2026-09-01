package com.dcuobot.api.character.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "The guild (league) a character belongs to.")
public class CharacterGuildResponse {
    @Schema(description = "Census guild id.")
    private String id;

    @Schema(description = "Guild name.")
    private String name;
}
