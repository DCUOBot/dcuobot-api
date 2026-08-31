package com.dcuobot.api.guild.api;

import com.dcuobot.api.guild.control.GuildService;
import com.dcuobot.api.guild.dto.GuildResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
