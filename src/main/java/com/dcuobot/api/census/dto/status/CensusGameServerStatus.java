package com.dcuobot.api.census.dto.status;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CensusGameServerStatus {
    private String name;

    @JsonProperty("game_code")
    private String gameCode;

    @JsonProperty("game_name")
    private String gameName;

    @JsonProperty("region_code")
    private String regionCode;

    @JsonProperty("region_name")
    private String regionName;

    @JsonProperty("last_reported_state")
    private String lastReportedState;

    @JsonProperty("last_reported_date")
    private String lastReportedDate;

    @JsonProperty("last_reported_time")
    private String lastReportedTime;
}
