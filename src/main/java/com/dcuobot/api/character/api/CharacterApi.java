package com.dcuobot.api.character.api;

import com.dcuobot.api.character.control.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/census/characters")
@RequiredArgsConstructor
public class CharacterApi {
    private final CharacterService characterService;

    @GetMapping
    public ResponseEntity<?> getCharacters(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "worldId", required = false) String worldId) {
        if (name != null && worldId != null) {
            return ResponseEntity.ok(characterService.getCharacter(name, worldId));
        }

        // TODO: characters ranking
        return ResponseEntity.ok().build();
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
