package com.dcuobot.api.gamedata.api;

import com.dcuobot.api.gamedata.control.GameDataService;
import com.dcuobot.api.gamedata.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/data")
@RequiredArgsConstructor
public class GameDataApi {
    private final GameDataService gameDataService;

    @GetMapping("/alignments")
    public ResponseEntity<List<AlignmentResponse>> getAlignments() {
        return ResponseEntity.ok(gameDataService.getAlignments());
    }

    @GetMapping("/allies")
    public ResponseEntity<List<AllyResponse>> getAllies() {
        return ResponseEntity.ok(gameDataService.getAllies());
    }

    @GetMapping("/artifacts")
    public ResponseEntity<List<ArtifactResponse>> getArtifacts() {
        return ResponseEntity.ok(gameDataService.getArtifacts());
    }

    @GetMapping("/genders")
    public ResponseEntity<List<GenderResponse>> getGenders() {
        return ResponseEntity.ok(gameDataService.getGenders());
    }

    @GetMapping("/guild-alignments")
    public ResponseEntity<List<GuildAlignmentResponse>> getGuildAlignments() {
        return ResponseEntity.ok(gameDataService.getGuildAlignments());
    }

    @GetMapping("/movement-modes")
    public ResponseEntity<List<MovementModeResponse>> getMovementModes() {
        return ResponseEntity.ok(gameDataService.getMovementModes());
    }

    @GetMapping("/personalities")
    public ResponseEntity<List<PersonalityResponse>> getPersonalities() {
        return ResponseEntity.ok(gameDataService.getPersonalities());
    }

    @GetMapping("/power-types")
    public ResponseEntity<List<PowerTypeResponse>> getPowerTypes() {
        return ResponseEntity.ok(gameDataService.getPowerTypes());
    }
}
