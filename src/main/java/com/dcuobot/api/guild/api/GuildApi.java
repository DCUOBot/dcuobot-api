package com.dcuobot.api.guild.api;

import com.dcuobot.api.common.exception.ErrorResponse;
import com.dcuobot.api.guild.control.GuildService;
import com.dcuobot.api.guild.dto.GuildResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Guilds",
        description = "League (guild) data. A league is either looked up directly by name and world, " +
                "or a leaderboard of leagues is fetched by ranking on an averaged/aggregate stat. Note: " +
                "the game itself and the underlying Census API call these \"leagues\"; this API and its " +
                "error messages use \"guild\" and \"league\" interchangeably."
)
@RestController
@RequestMapping("/v1/census/guilds")
@RequiredArgsConstructor
public class GuildApi {
    private final GuildService guildService;

    @Operation(
            summary = "Look up a guild, or rank guilds by a stat",
            description = """
                    Operates in one of two mutually exclusive modes, chosen by which parameters are supplied:

                    - **Lookup**: pass `name` and `worldId` together to fetch a single guild, including its \
                    averaged roster stats and full member list. Returns a single guild object.
                    - **Ranking**: pass `sort` and `sortDirection` (optionally with `worldId`) to fetch a \
                    leaderboard of guilds ordered by that stat. Omitting `worldId` ranks across all worlds. \
                    Only guilds with at least 20 members are eligible for ranking. Returns an array of guilds.

                    Passing neither `name`+`worldId` nor `sort`+`sortDirection` returns `400 Bad Request`."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A single guild (lookup mode) or an array of ranked guilds (ranking mode).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {GuildResponse.class, GuildResponse[].class})
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Neither name/worldId nor sort/sortDirection was provided, or sort is " +
                            "not a recognized stat.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No guild matches the given name and world (lookup mode only).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "The Census API is unreachable or returned malformed data (lookup mode only).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<?> getGuilds(
            @Parameter(description = "Guild name to look up. Exact match. Must be paired with worldId.")
            @RequestParam(value = "name", required = false) String name,

            @Parameter(description = "World (server) id to scope the lookup or ranking to. One of `2` " +
                    "(US-PC), `4` (EU-PC), `10` (US-PS), `11` (EU-PS), `5001` (US-Xbox). Required " +
                    "alongside name in lookup mode; optional in ranking mode, where omitting it ranks " +
                    "across all worlds.")
            @RequestParam(value = "worldId", required = false) String worldId,

            @Parameter(description = "Stat to rank guilds by, enabling ranking mode. One of " +
                    "`memberCount`, `averageSkillPoints`, `averageCombatRating`, `averagePvpCombatRating`.")
            @RequestParam(value = "sort", required = false) String sort,

            @Parameter(description = "Sort direction for ranking mode. Required when sort is provided.")
            @RequestParam(value = "sortDirection", required = false) @Valid Sort.Direction sortDirection) {
        if (name != null && worldId != null) {
            return ResponseEntity.ok(guildService.getGuild(name, worldId));
        }

        if (sort == null) {
            throw new IllegalArgumentException("Either name and worldId or sort must be provided.");
        }

        if (sortDirection == null) {
            throw new IllegalArgumentException("sortDirection must be provided.");
        }

        List<GuildResponse> guilds = guildService.getGuildRanking(sort, sortDirection, worldId);

        return ResponseEntity.ok(guilds);
    }
}
