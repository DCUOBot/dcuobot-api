package com.dcuobot.api.census.dto.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CensusGameServerStatusList {
    @JsonProperty("game_server_status_list")
    private Collection<CensusGameServerStatus> gameServerStatusList;

    private int returned;
}
