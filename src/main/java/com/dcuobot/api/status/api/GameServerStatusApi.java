package com.dcuobot.api.status.api;

import com.dcuobot.api.status.control.GameServerStatusService;
import com.dcuobot.api.status.dto.GameServerStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/census/status")
@RequiredArgsConstructor
public class GameServerStatusApi {
    private final GameServerStatusService gameServerStatusService;

    @GetMapping("/game-servers")
    public ResponseEntity<List<GameServerStatusResponse>> getGameServerStatus() {
        return ResponseEntity.ok(gameServerStatusService.getGameServerStatus());
    }
}
