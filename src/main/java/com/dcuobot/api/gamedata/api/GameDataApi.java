package com.dcuobot.api.gamedata.api;

import com.dcuobot.api.gamedata.control.GameDataService;
import com.dcuobot.api.gamedata.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "Game Data",
        description = "Static reference data used to resolve the census ids returned by the character " +
                "and guild endpoints (e.g. a character's `power_type` field is looked up here by its " +
                "census id). Maintained by DCUOBot rather than sourced from Census, so these endpoints " +
                "don't depend on Census availability."
)
@RestController
@RequestMapping("/v1/data")
@RequiredArgsConstructor
public class GameDataApi {
    private final GameDataService gameDataService;

    @Operation(summary = "List character alignments", description = "e.g. Hero, Villain.")
    @ApiResponse(responseCode = "200", description = "All known alignments.")
    @GetMapping("/alignments")
    public ResponseEntity<List<AlignmentResponse>> getAlignments() {
        return ResponseEntity.ok(gameDataService.getAlignments());
    }

    @Operation(summary = "List allies", description = "Equippable ally items, including the " +
            "Discord emoji used to represent each in the DCUOBot Discord bot.")
    @ApiResponse(responseCode = "200", description = "All known allies.")
    @GetMapping("/allies")
    public ResponseEntity<List<AllyResponse>> getAllies() {
        return ResponseEntity.ok(gameDataService.getAllies());
    }

    @Operation(summary = "List artifacts", description = "Equippable artifact items, including the " +
            "Discord emoji used to represent each in the DCUOBot Discord bot.")
    @ApiResponse(responseCode = "200", description = "All known artifacts.")
    @GetMapping("/artifacts")
    public ResponseEntity<List<ArtifactResponse>> getArtifacts() {
        return ResponseEntity.ok(gameDataService.getArtifacts());
    }

    @Operation(summary = "List character genders", description = "Includes the placeholder image URL " +
            "used as a character's fallback paperdoll image.")
    @ApiResponse(responseCode = "200", description = "All known genders.")
    @GetMapping("/genders")
    public ResponseEntity<List<GenderResponse>> getGenders() {
        return ResponseEntity.ok(gameDataService.getGenders());
    }

    @Operation(summary = "List guild alignments", description = "e.g. Hero, Villain, Vigilante — the " +
            "alignment values used for guilds/leagues specifically, distinct from character alignments.")
    @ApiResponse(responseCode = "200", description = "All known guild alignments.")
    @GetMapping("/guild-alignments")
    public ResponseEntity<List<GuildAlignmentResponse>> getGuildAlignments() {
        return ResponseEntity.ok(gameDataService.getGuildAlignments());
    }

    @Operation(summary = "List movement modes", description = "e.g. Flight, Acrobatics, Super Speed.")
    @ApiResponse(responseCode = "200", description = "All known movement modes.")
    @GetMapping("/movement-modes")
    public ResponseEntity<List<MovementModeResponse>> getMovementModes() {
        return ResponseEntity.ok(gameDataService.getMovementModes());
    }

    @Operation(summary = "List personalities", description = "A character's roleplay personality trait.")
    @ApiResponse(responseCode = "200", description = "All known personalities.")
    @GetMapping("/personalities")
    public ResponseEntity<List<PersonalityResponse>> getPersonalities() {
        return ResponseEntity.ok(gameDataService.getPersonalities());
    }

    @Operation(summary = "List power types", description = "e.g. Fire, Ice, Gadgets, Light.")
    @ApiResponse(responseCode = "200", description = "All known power types.")
    @GetMapping("/power-types")
    public ResponseEntity<List<PowerTypeResponse>> getPowerTypes() {
        return ResponseEntity.ok(gameDataService.getPowerTypes());
    }
}
