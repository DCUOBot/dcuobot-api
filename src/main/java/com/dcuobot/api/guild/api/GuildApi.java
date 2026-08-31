package com.dcuobot.api.guild.api;

import com.dcuobot.api.guild.control.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/census/guilds")
@RequiredArgsConstructor
public class GuildApi {
    private final GuildService guildService;

    @GetMapping
    public ResponseEntity<?> getGuilds(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "worldId", required = false) String worldId,
                                       @RequestParam(value = "sort", required = false) String sort,
                                       @RequestParam(value = "sortDirection", required = false) @Valid Sort.Direction sortDirection) {
        if (name != null && worldId != null) {
            return ResponseEntity.ok(guildService.getGuild(name, worldId));
        }

        // TODO: guilds ranking

        return ResponseEntity.ok().build();
    }
}
