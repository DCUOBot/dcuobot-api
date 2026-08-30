package com.dcuobot.api.common.worldid;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WorldIdHelpers {
    private static final Set<String> WORLD_IDS = Set.of("2", "4", "10", "11", "5001");

    public boolean isValidWorldId(String worldId) {
        return WORLD_IDS.contains(worldId);
    }
}
