package com.dcuobot.api.census.dto.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CensusGuildRosterList {
    @JsonProperty("guild_roster_list")
    private List<CensusGuildRoster> guildRosterList;

    private int returned;
}
