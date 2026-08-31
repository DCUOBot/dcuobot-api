package com.dcuobot.api.census.dto.guild;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class CensusGuildList {
    @JsonProperty("guild_list")
    private Collection<CensusGuild> guildList;

    private int returned;
}
