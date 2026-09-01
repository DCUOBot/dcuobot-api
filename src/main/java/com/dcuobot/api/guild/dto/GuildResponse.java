package com.dcuobot.api.guild.dto;

import com.dcuobot.api.guild.entity.Guild;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "A guild (league).")
public class GuildResponse {
    @JsonProperty("guild_id")
    @Schema(description = "Census guild id.")
    private String guildId;

    @JsonProperty("world_id")
    @Schema(description = "World (server) id the guild belongs to.", example = "2")
    private String worldId;

    @Schema(description = "Guild name.")
    private String name;

    @Schema(description = "Guild alignment, e.g. Hero, Villain, Vigilante.")
    private String alignment;

    @JsonProperty("member_count")
    @Schema(description = "Number of members in the guild's roster.")
    private int memberCount;

    @JsonProperty("average_skill_points")
    @Schema(description = "Average skill points across the guild's roster.")
    private double averageSkillPoints;

    @JsonProperty("average_combat_rating")
    @Schema(description = "Average PvE combat rating across the guild's roster.")
    private double averageCombatRating;

    @JsonProperty("average_pvp_combat_rating")
    @Schema(description = "Average PvP combat rating across the guild's roster.")
    private double averagePvpCombatRating;

    @Schema(description = "Guild's full member roster. Null in ranking mode.")
    private List<GuildCharacterResponse> characters;

    public static GuildResponse fromEntity(Guild guild) {
        GuildResponse response = new GuildResponse();
        response.setGuildId(guild.getCensusId());
        response.setName(guild.getName());
        response.setAlignment(guild.getAlignment().getName());
        response.setWorldId(guild.getWorldId());
        response.setMemberCount(guild.getMemberCount());
        response.setAverageSkillPoints(guild.getAverageSkillPoints());
        response.setAverageCombatRating(guild.getAverageCombatRating());
        response.setAveragePvpCombatRating(guild.getAveragePvpCombatRating());
        return response;
    }
}
