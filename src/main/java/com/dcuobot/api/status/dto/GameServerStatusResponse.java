package com.dcuobot.api.status.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GameServerStatusResponse {
    @JsonProperty("server_name")
    private String serverName;

    private GameServerStatus status;

    private String population;
}
