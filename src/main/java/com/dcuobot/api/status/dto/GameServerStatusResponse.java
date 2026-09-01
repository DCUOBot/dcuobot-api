package com.dcuobot.api.status.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Status of a single DC Universe Online game server.")
public class GameServerStatusResponse {
    @JsonProperty("server_name")
    @Schema(description = "Display name of the game server.", example = "US-PC")
    private String serverName;

    @Schema(description = "Current online/locked/offline status.")
    private GameServerStatus status;

    @Schema(description = "Current population level, e.g. Low, Medium, High.")
    private String population;
}
