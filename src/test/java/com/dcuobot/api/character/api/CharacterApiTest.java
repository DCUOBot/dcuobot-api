package com.dcuobot.api.character.api;

import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.character.control.CharacterService;
import com.dcuobot.api.character.dto.*;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CharacterApi.class)
class CharacterApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CharacterService characterService;

    @Test
    void getCharacters_returnsCharacterResponseAsJson_whenNameAndWorldIdAreProvided() throws Exception {
        when(characterService.getCharacter("Batman", "1000")).thenReturn(fullCharacterResponse());

        mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.character_id").value("100"))
                .andExpect(jsonPath("$.world_id").value("1000"))
                .andExpect(jsonPath("$.name").value("Batman"))
                .andExpect(jsonPath("$.alignment").value("Good"))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.power_type").value("Fire"))
                .andExpect(jsonPath("$.movement_mode").value("Flight"))
                .andExpect(jsonPath("$.personality").value("Fierce"))
                .andExpect(jsonPath("$.combat_rating").value(500))
                .andExpect(jsonPath("$.pvp_combat_rating").value(400))
                .andExpect(jsonPath("$.skill_points").value(200))
                .andExpect(jsonPath("$.stats.health").value(10000))
                .andExpect(jsonPath("$.guild.id").value("55"))
                .andExpect(jsonPath("$.guild.name").value("Justice League"))
                .andExpect(jsonPath("$.artifacts[0].id").value("art1"))
                .andExpect(jsonPath("$.artifacts[0].name").value("Heart of Darkness"))
                .andExpect(jsonPath("$.artifacts[0].image_url").value("https://example.com/artifact.png"))
                .andExpect(jsonPath("$.artifacts[0].discord_emoji_id").value("123456789"))
                .andExpect(jsonPath("$.allies[0].id").value("ally1"))
                .andExpect(jsonPath("$.allies[0].name").value("Robin"))
                .andExpect(jsonPath("$.allies[0].combat").value(true))
                .andExpect(jsonPath("$.image.url")
                        .value("https://dcuo.bot/api/v1/census/characters/100/image?genderId=0"))
                .andExpect(jsonPath("$.image.alt_url").value("https://example.com/gender.png"));
    }

    @Test
    void getCharacters_returnsEmptyOkResponse_whenNameIsMissing() throws Exception {
        mockMvc.perform(get("/v1/census/characters").param("worldId", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_returnsEmptyOkResponse_whenWorldIdIsMissing() throws Exception {
        mockMvc.perform(get("/v1/census/characters").param("name", "Batman"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_returnsEmptyOkResponse_whenNoParamsProvided() throws Exception {
        mockMvc.perform(get("/v1/census/characters"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_propagatesException_whenCharacterNotFound() {
        when(characterService.getCharacter("Batman", "1000")).thenThrow(new CharacterNotFoundException());

        assertThatThrownBy(() ->
                mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "1000")))
                .hasRootCauseInstanceOf(CharacterNotFoundException.class);
    }

    @Test
    void getCharacters_propagatesException_whenWorldIdIsInvalid() {
        when(characterService.getCharacter("Batman", "9999")).thenThrow(new InvalidWorldIdException());

        assertThatThrownBy(() ->
                mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "9999")))
                .hasRootCauseInstanceOf(InvalidWorldIdException.class);
    }

    @Test
    void getCharacters_propagatesException_whenCensusIsUnreachable() {
        when(characterService.getCharacter("Batman", "1000")).thenThrow(new CensusException());

        assertThatThrownBy(() ->
                mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "1000")))
                .hasRootCauseInstanceOf(CensusException.class);
    }

    private CharacterResponse fullCharacterResponse() {
        CharacterResponse response = new CharacterResponse();
        response.setCharacterId("100");
        response.setWorldId("1000");
        response.setName("Batman");
        response.setAlignment("Good");
        response.setGender("Male");
        response.setPowerType("Fire");
        response.setMovementMode("Flight");
        response.setPersonality("Fierce");
        response.setCombatRating(500);
        response.setPvpCombatRating(400);
        response.setSkillPoints(200);

        CharacterStatsResponse stats = new CharacterStatsResponse();
        stats.setHealth(10000);
        response.setStats(stats);

        CharacterGuildResponse guild = new CharacterGuildResponse();
        guild.setId("55");
        guild.setName("Justice League");
        response.setGuild(guild);

        CharacterArtifactResponse artifact = new CharacterArtifactResponse();
        artifact.setId("art1");
        artifact.setName("Heart of Darkness");
        artifact.setImageUrl("https://example.com/artifact.png");
        artifact.setDiscordEmojiId("123456789");
        response.setArtifacts(List.of(artifact));

        CharacterAllyResponse ally = new CharacterAllyResponse();
        ally.setId("ally1");
        ally.setName("Robin");
        ally.setCombat(true);
        response.setAllies(List.of(ally));

        CharacterImageResponse image = new CharacterImageResponse();
        image.setUrl("https://dcuo.bot/api/v1/census/characters/100/image?genderId=0");
        image.setAltUrl("https://example.com/gender.png");
        response.setImage(image);

        return response;
    }
}
