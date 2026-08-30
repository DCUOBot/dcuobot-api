package com.dcuobot.api.common.listener;

import com.dcuobot.api.gamedata.control.GameDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationReadyListener {
    private final GameDataService gameDataService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        gameDataService.loadDefaultAlignments();
        gameDataService.loadDefaultAllies();
        gameDataService.loadDefaultArtifacts();
        gameDataService.loadDefaultGenders();
        gameDataService.loadDefaultGuildAlignments();
        gameDataService.loadDefaultMovementModes();
        gameDataService.loadDefaultPersonalities();
        gameDataService.loadDefaultPowerTypes();
    }
}
