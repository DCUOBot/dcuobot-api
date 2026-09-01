package com.dcuobot.api.status.api;

import com.dcuobot.api.status.control.GameServerStatusService;
import com.dcuobot.api.status.dto.GameServerStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Server Status", description = "Live status of DC Universe Online's game servers.")
@RestController
@RequestMapping("/v1/census/status")
@RequiredArgsConstructor
public class GameServerStatusApi {
    private final GameServerStatusService gameServerStatusService;

    @Operation(
            summary = "Get the status of every game server",
            description = "Returns the online/locked/offline status and population level for every " +
                    "DC Universe Online game server (world)."
    )
    @ApiResponse(responseCode = "200", description = "Status of every game server.")
    @GetMapping("/game-servers")
    public ResponseEntity<List<GameServerStatusResponse>> getGameServerStatus() {
        return ResponseEntity.ok(gameServerStatusService.getGameServerStatus());
    }
}
