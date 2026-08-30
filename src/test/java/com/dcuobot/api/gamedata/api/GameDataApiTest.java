package com.dcuobot.api.gamedata.api;

import com.dcuobot.api.gamedata.control.GameDataService;
import com.dcuobot.api.gamedata.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameDataApi.class)
class GameDataApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameDataService gameDataService;

    @Test
    void getAlignments_returnsPersistedAlignmentsAsJson() throws Exception {
        AlignmentResponse alignment = new AlignmentResponse();
        alignment.setCensusId("1");
        alignment.setName("Good");
        when(gameDataService.getAlignments()).thenReturn(List.of(alignment));

        mockMvc.perform(get("/v1/data/alignments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Good"));
    }

    @Test
    void getAlignments_returnsEmptyArrayWhenNoneArePersisted() throws Exception {
        when(gameDataService.getAlignments()).thenReturn(List.of());

        mockMvc.perform(get("/v1/data/alignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAllies_returnsPersistedAlliesAsJson() throws Exception {
        AllyResponse ally = new AllyResponse();
        ally.setCensusId("1");
        ally.setName("Batman");
        when(gameDataService.getAllies()).thenReturn(List.of(ally));

        mockMvc.perform(get("/v1/data/allies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Batman"));
    }

    @Test
    void getArtifacts_returnsPersistedArtifactsAsJson() throws Exception {
        ArtifactResponse artifact = new ArtifactResponse();
        artifact.setCensusId("1");
        artifact.setName("Heart of Darkness");
        artifact.setImageUrl("https://example.com/artifact.png");
        artifact.setDiscordEmojiId("123456789");
        when(gameDataService.getArtifacts()).thenReturn(List.of(artifact));

        mockMvc.perform(get("/v1/data/artifacts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Heart of Darkness"))
                .andExpect(jsonPath("$[0].image_url").value("https://example.com/artifact.png"))
                .andExpect(jsonPath("$[0].discord_emoji_id").value("123456789"));
    }

    @Test
    void getGenders_returnsPersistedGendersAsJson() throws Exception {
        GenderResponse gender = new GenderResponse();
        gender.setCensusId("1");
        gender.setName("Female");
        gender.setImageUrl("https://example.com/gender.png");
        when(gameDataService.getGenders()).thenReturn(List.of(gender));

        mockMvc.perform(get("/v1/data/genders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Female"))
                .andExpect(jsonPath("$[0].image_url").value("https://example.com/gender.png"));
    }

    @Test
    void getGuildAlignments_returnsPersistedGuildAlignmentsAsJson() throws Exception {
        GuildAlignmentResponse guildAlignment = new GuildAlignmentResponse();
        guildAlignment.setCensusId("1");
        guildAlignment.setName("Hero");
        when(gameDataService.getGuildAlignments()).thenReturn(List.of(guildAlignment));

        mockMvc.perform(get("/v1/data/guild-alignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Hero"));
    }

    @Test
    void getMovementModes_returnsPersistedMovementModesAsJson() throws Exception {
        MovementModeResponse movementMode = new MovementModeResponse();
        movementMode.setCensusId("1");
        movementMode.setName("Flight");
        when(gameDataService.getMovementModes()).thenReturn(List.of(movementMode));

        mockMvc.perform(get("/v1/data/movement-modes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Flight"));
    }

    @Test
    void getPersonalities_returnsPersistedPersonalitiesAsJson() throws Exception {
        PersonalityResponse personality = new PersonalityResponse();
        personality.setCensusId("1");
        personality.setName("Fierce");
        when(gameDataService.getPersonalities()).thenReturn(List.of(personality));

        mockMvc.perform(get("/v1/data/personalities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Fierce"));
    }

    @Test
    void getPowerTypes_returnsPersistedPowerTypesAsJson() throws Exception {
        PowerTypeResponse powerType = new PowerTypeResponse();
        powerType.setCensusId("1");
        powerType.setName("Fire");
        when(gameDataService.getPowerTypes()).thenReturn(List.of(powerType));

        mockMvc.perform(get("/v1/data/power-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].census_id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Fire"));
    }
}
