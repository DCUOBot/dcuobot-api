package com.dcuobot.api.status.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Online status of a game server.")
public enum GameServerStatus {
    ONLINE,
    LOCKED,
    OFFLINE
}
