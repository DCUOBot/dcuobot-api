package com.dcuobot.api.character.api;

import com.dcuobot.api.character.control.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
