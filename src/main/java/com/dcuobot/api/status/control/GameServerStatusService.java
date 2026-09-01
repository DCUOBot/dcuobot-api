package com.dcuobot.api.status.control;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.status.CensusGameServerStatus;
import com.dcuobot.api.census.dto.status.CensusGameServerStatusList;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.config.CacheConfig;
import com.dcuobot.api.status.dto.GameServerStatus;
import com.dcuobot.api.status.dto.GameServerStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServerStatusService {
    private final CensusClient censusClient;

    /**
     * Fetches the current status of every game server from Census.
     *
     * @throws CensusException if the Census API is unreachable or returns malformed data
     */
    @Cacheable(CacheConfig.SERVER_STATUS_CACHE)
    public List<GameServerStatusResponse> getGameServerStatus() throws CensusException {
        CensusGameServerStatusList statusList = censusClient.getGameServerStatus();

        if (statusList == null || statusList.getGameServerStatusList() == null) {
            throw new CensusException();
        }

        return statusList.getGameServerStatusList()
                .stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps a Census game server to its response DTO.
     */
    private GameServerStatusResponse buildResponse(CensusGameServerStatus census) {
        Resolved resolved = resolve(census.getLastReportedState().toLowerCase());

        GameServerStatusResponse response = new GameServerStatusResponse();
        response.setServerName(census.getName());
        response.setStatus(resolved.status());
        response.setPopulation(resolved.population());
        return response;
    }

    /**
     * Maps a Census {@code last_reported_state} to a status/population pair in one place, since
     * the population label is only ever meaningful for the "online" states.
     */
    private static Resolved resolve(String state) {
        return switch (state) {
            case "locked" -> new Resolved(GameServerStatus.LOCKED, "---");
            case "high", "medium", "low" ->
                    new Resolved(GameServerStatus.ONLINE, Character.toUpperCase(state.charAt(0)) + state.substring(1));
            default -> new Resolved(GameServerStatus.OFFLINE, "---");
        };
    }

    private record Resolved(GameServerStatus status, String population) {
    }
}
