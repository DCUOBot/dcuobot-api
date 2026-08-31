package com.dcuobot.api.character.api;

import com.dcuobot.api.character.control.CharacterService;
import com.dcuobot.api.character.dto.CharacterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/v1/census/characters")
@RequiredArgsConstructor
public class CharacterApi {
    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<?> getCharacters(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "worldId", required = false) String worldId,
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

    @GetMapping(
            value = "/{characterId}/image",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> getCharacterImage(@PathVariable String characterId,
                                               @RequestParam(required = false) String genderId) throws IOException {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(characterService.getCharacterImage(characterId, genderId));
    }
}
