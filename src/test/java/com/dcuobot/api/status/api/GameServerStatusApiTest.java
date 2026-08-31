package com.dcuobot.api.status.api;

import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.status.control.GameServerStatusService;
import com.dcuobot.api.status.dto.GameServerStatus;
import com.dcuobot.api.status.dto.GameServerStatusResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameServerStatusApi.class)
class GameServerStatusApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameServerStatusService gameServerStatusService;

    @Test
    void getGameServerStatus_returnsGameServerStatusListAsJson() throws Exception {
        when(gameServerStatusService.getGameServerStatus()).thenReturn(List.of(uspc1(), usps1()));

        mockMvc.perform(get("/v1/census/status/game-servers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].server_name").value("USPC1"))
                .andExpect(jsonPath("$[0].status").value("ONLINE"))
                .andExpect(jsonPath("$[0].population").value("High"))
                .andExpect(jsonPath("$[1].server_name").value("USPS1"))
                .andExpect(jsonPath("$[1].status").value("LOCKED"))
                .andExpect(jsonPath("$[1].population").value("---"));
    }

    @Test
    void getGameServerStatus_returnsEmptyJsonArray_whenNoServersAreReported() throws Exception {
        when(gameServerStatusService.getGameServerStatus()).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/status/game-servers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void getGameServerStatus_returnsBadGatewayError_whenCensusIsUnreachable() throws Exception {
        when(gameServerStatusService.getGameServerStatus()).thenThrow(new CensusException());

        mockMvc.perform(get("/v1/census/status/game-servers"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_GATEWAY.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("The Daybreak Games Census API did not respond."))
                .andExpect(jsonPath("$.path").value("/v1/census/status/game-servers"));
    }

    private GameServerStatusResponse uspc1() {
        GameServerStatusResponse response = new GameServerStatusResponse();
        response.setServerName("USPC1");
        response.setStatus(GameServerStatus.ONLINE);
        response.setPopulation("High");
        return response;
    }

    private GameServerStatusResponse usps1() {
        GameServerStatusResponse response = new GameServerStatusResponse();
        response.setServerName("USPS1");
        response.setStatus(GameServerStatus.LOCKED);
        response.setPopulation("---");
        return response;
    }
}
