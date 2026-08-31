package com.dcuobot.api.status.control;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.status.CensusGameServerStatus;
import com.dcuobot.api.census.dto.status.CensusGameServerStatusList;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.status.dto.GameServerStatus;
import com.dcuobot.api.status.dto.GameServerStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServerStatusServiceTest {

    @Mock
    private CensusClient censusClient;

    private GameServerStatusService gameServerStatusService;

    @BeforeEach
    void setUp() {
        gameServerStatusService = new GameServerStatusService(censusClient);
    }

    @Test
    void getGameServerStatus_throwsCensusException_whenResponseIsNull() {
        when(censusClient.getGameServerStatus()).thenReturn(null);

        assertThatThrownBy(() -> gameServerStatusService.getGameServerStatus())
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getGameServerStatus_throwsCensusException_whenGameServerStatusListIsNull() {
        CensusGameServerStatusList statusList = new CensusGameServerStatusList();
        statusList.setGameServerStatusList(null);
        when(censusClient.getGameServerStatus()).thenReturn(statusList);

        assertThatThrownBy(() -> gameServerStatusService.getGameServerStatus())
                .isInstanceOf(CensusException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "locked, LOCKED, ---",
            "high, ONLINE, High",
            "medium, ONLINE, Medium",
            "low, ONLINE, Low",
            "offline, OFFLINE, ---",
    })
    void getGameServerStatus_resolvesStatusAndPopulation_fromLastReportedState(
            String lastReportedState, GameServerStatus expectedStatus, String expectedPopulation) {
        stubStatusList(server("USPC1", lastReportedState));

        GameServerStatusResponse response = gameServerStatusService.getGameServerStatus().getFirst();

        assertThat(response.getServerName()).isEqualTo("USPC1");
        assertThat(response.getStatus()).isEqualTo(expectedStatus);
        assertThat(response.getPopulation()).isEqualTo(expectedPopulation);
    }

    @Test
    void getGameServerStatus_isCaseInsensitive_toLastReportedState() {
        stubStatusList(server("USPC1", "HIGH"));

        GameServerStatusResponse response = gameServerStatusService.getGameServerStatus().getFirst();

        assertThat(response.getStatus()).isEqualTo(GameServerStatus.ONLINE);
        assertThat(response.getPopulation()).isEqualTo("High");
    }

    @Test
    void getGameServerStatus_mapsEachServerInTheList() {
        stubStatusList(server("USPC1", "high"), server("USPS1", "locked"));

        List<GameServerStatusResponse> responses = gameServerStatusService.getGameServerStatus();

        assertThat(responses)
                .extracting(GameServerStatusResponse::getServerName, GameServerStatusResponse::getStatus,
                        GameServerStatusResponse::getPopulation)
                .containsExactly(
                        tuple("USPC1", GameServerStatus.ONLINE, "High"),
                        tuple("USPS1", GameServerStatus.LOCKED, "---"));
    }

    @Test
    void getGameServerStatus_returnsEmptyList_whenNoServersAreReported() {
        stubStatusList();

        assertThat(gameServerStatusService.getGameServerStatus()).isEmpty();
    }

    private void stubStatusList(CensusGameServerStatus... servers) {
        CensusGameServerStatusList statusList = new CensusGameServerStatusList();
        statusList.setGameServerStatusList(List.of(servers));
        when(censusClient.getGameServerStatus()).thenReturn(statusList);
    }

    private CensusGameServerStatus server(String name, String lastReportedState) {
        CensusGameServerStatus status = new CensusGameServerStatus();
        status.setName(name);
        status.setLastReportedState(lastReportedState);
        return status;
    }
}
