package com.dcuobot.api.character.api;

import com.dcuobot.api.character.control.CharacterService;
import com.dcuobot.api.character.dto.CharacterResponse;
import com.dcuobot.api.common.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@Tag(
        name = "Characters",
        description = "Live character data sourced from the Census API. A character is either looked " +
                "up directly by name and world, or a leaderboard of characters is fetched by ranking " +
                "an entire world (or all worlds) by a stat."
)
@RestController
@RequestMapping("/v1/census/characters")
@RequiredArgsConstructor
public class CharacterApi {
    private final CharacterService characterService;

    @Operation(
            summary = "Look up a character, or rank characters by a stat",
            description = """
                    Operates in one of two mutually exclusive modes, chosen by which parameters are supplied:

                    - **Lookup**: pass `name` and `worldId` together to fetch a single character, including \
                    its stats, equipped artifacts/allies, and league (guild). Returns a single character object.
                    - **Ranking**: pass `sort` (optionally with `worldId`) to fetch a leaderboard of \
                    characters ordered by that stat, descending. Omitting `worldId` ranks across all worlds. \
                    Returns an array of characters, without league/artifacts/allies data.

                    Passing neither `name`+`worldId` nor `sort` returns `400 Bad Request`."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A single character (lookup mode) or an array of ranked characters " +
                            "(ranking mode).",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(oneOf = {CharacterResponse.class, CharacterResponse[].class})
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Neither name/worldId nor sort was provided, or sort is not a " +
                            "recognized stat.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No character matches the given name and world (lookup mode only).",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "The Census API is unreachable or returned malformed data.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<?> getCharacters(
            @Parameter(description = "Character name to look up. Exact match unless under 3 characters, " +
                    "in which case a wildcard search is used. Must be paired with worldId.")
            @RequestParam(value = "name", required = false) String name,

            @Parameter(description = "World (server) id to scope the lookup or ranking to. One of `2` " +
                    "(US-PC), `4` (EU-PC), `10` (US-PS), `11` (EU-PS), `5001` (US-Xbox). Required " +
                    "alongside name in lookup mode; optional in ranking mode, where omitting it ranks " +
                    "across all worlds.")
            @RequestParam(value = "worldId", required = false) String worldId,

            @Parameter(description = "Stat to rank characters by, enabling ranking mode. One of " +
                    "`skill_points`, `combat_rating`, `pvp_combat_rating`, `max_health`, `max_power`, " +
                    "`toughness`, `might`, `precision`, `defense`, `dominance`, `restoration`, " +
                    "`vitalization`. Case-insensitive.")
            @RequestParam(value = "sort", required = false) String sort) {
        if (name != null && worldId != null) {
            return ResponseEntity.ok(characterService.getCharacter(name, worldId));
        }

        if (sort == null) {
            throw new IllegalArgumentException("Either name and worldId or sort must be provided.");
        }

        Collection<CharacterResponse> characters = characterService.getCharacterRanking(
                (worldId == null || (!worldId.equals("2") && !worldId.equals("4") && !worldId.equals("10") && !worldId.equals("11") && !worldId.equals("5001"))) ? null : worldId,
                sort.toLowerCase()
        );

        return ResponseEntity.ok(characters);
    }

    @Operation(
            summary = "Get a character's paperdoll image",
            description = "Fetches the character's rendered paperdoll image. If no rendered image is " +
                    "available yet, falls back to a generic gender-based placeholder image."
    )
    @ApiResponse(
            responseCode = "200",
            description = "PNG-encoded character image.",
            content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE, schema = @Schema(type = "string", format = "binary"))
    )
    @GetMapping(
            value = "/{characterId}/image",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> getCharacterImage(
            @Parameter(description = "Census character id, as returned in `character_id` by the " +
                    "character lookup/ranking endpoint.", in = ParameterIn.PATH)
            @PathVariable String characterId,

            @Parameter(description = "Census gender id, used to resolve the fallback placeholder image " +
                    "when no rendered paperdoll exists yet. If omitted, the gender is looked up from " +
                    "Census, at the cost of an extra request.")
            @RequestParam(required = false) String genderId) throws IOException {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(characterService.getCharacterImage(characterId, genderId));
    }
}
