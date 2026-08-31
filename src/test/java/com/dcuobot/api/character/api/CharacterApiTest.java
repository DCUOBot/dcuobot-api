package com.dcuobot.api.character.api;

import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.character.control.CharacterService;
import com.dcuobot.api.character.dto.*;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.common.sort.InvalidSortCriteriaException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    void getCharacters_returnsBadRequestError_whenNameIsProvidedWithoutWorldIdOrSort() throws Exception {
        mockMvc.perform(get("/v1/census/characters").param("name", "Batman"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_returnsBadRequestError_whenWorldIdIsProvidedWithoutNameOrSort() throws Exception {
        mockMvc.perform(get("/v1/census/characters").param("worldId", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_returnsBadRequestError_whenNoParamsProvided() throws Exception {
        mockMvc.perform(get("/v1/census/characters"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));

        verifyNoInteractions(characterService);
    }

    @Test
    void getCharacters_returnsRanking_whenOnlySortIsProvided() throws Exception {
        when(characterService.getCharacterRanking(null, "combat_rating")).thenReturn(List.of(rankingCharacterResponse()));

        mockMvc.perform(get("/v1/census/characters").param("sort", "combat_rating"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].character_id").value("100"))
                .andExpect(jsonPath("$[0].world_id").value("1000"))
                .andExpect(jsonPath("$[0].name").value("Batman"))
                .andExpect(jsonPath("$[0].combat_rating").value(500))
                .andExpect(jsonPath("$[0].guild").doesNotExist())
                .andExpect(jsonPath("$[0].artifacts").doesNotExist());

        verify(characterService, never()).getCharacter(anyString(), anyString());
    }

    @Test
    void getCharacters_lowercasesSort_whenSortHasMixedCase() throws Exception {
        when(characterService.getCharacterRanking(null, "combat_rating")).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/characters").param("sort", "Combat_Rating"))
                .andExpect(status().isOk());

        verify(characterService).getCharacterRanking(null, "combat_rating");
    }

    @Test
    void getCharacters_ignoresNameParam_whenWorldIdIsMissingButSortIsProvided() throws Exception {
        when(characterService.getCharacterRanking(null, "combat_rating")).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("sort", "combat_rating"))
                .andExpect(status().isOk());

        verify(characterService, never()).getCharacter(anyString(), anyString());
        verify(characterService).getCharacterRanking(null, "combat_rating");
    }

    @ParameterizedTest
    @ValueSource(strings = {"2", "4", "10", "11", "5001"})
    void getCharacters_passesWorldIdThrough_whenWorldIdIsAnAllowedRankingWorld(String worldId) throws Exception {
        when(characterService.getCharacterRanking(worldId, "combat_rating")).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/characters").param("worldId", worldId).param("sort", "combat_rating"))
                .andExpect(status().isOk());

        verify(characterService).getCharacterRanking(worldId, "combat_rating");
    }

    @Test
    void getCharacters_passesNullWorldId_whenWorldIdIsNotAnAllowedRankingWorld() throws Exception {
        when(characterService.getCharacterRanking(null, "combat_rating")).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/characters").param("worldId", "9999").param("sort", "combat_rating"))
                .andExpect(status().isOk());

        verify(characterService).getCharacterRanking(null, "combat_rating");
    }

    @Test
    void getCharacters_returnsBadRequestError_whenSortCriteriaIsInvalid() throws Exception {
        when(characterService.getCharacterRanking(null, "bogus")).thenThrow(new InvalidSortCriteriaException());

        mockMvc.perform(get("/v1/census/characters").param("sort", "bogus"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid sort criteria."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));
    }

    @Test
    void getCharacters_returnsBadGatewayError_whenRankingCensusIsUnreachable() throws Exception {
        when(characterService.getCharacterRanking(null, "combat_rating")).thenThrow(new CensusException());

        mockMvc.perform(get("/v1/census/characters").param("sort", "combat_rating"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(jsonPath("$.message").value("The Daybreak Games Census API did not respond."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));
    }

    @Test
    void getCharacters_returnsNotFoundError_whenCharacterNotFound() throws Exception {
        when(characterService.getCharacter("Batman", "1000")).thenThrow(new CharacterNotFoundException());

        mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Character not found."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));
    }

    @Test
    void getCharacters_returnsBadRequestError_whenWorldIdIsInvalid() throws Exception {
        when(characterService.getCharacter("Batman", "9999")).thenThrow(new InvalidWorldIdException());

        mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Invalid world id."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));
    }

    @Test
    void getCharacters_returnsBadGatewayError_whenCensusIsUnreachable() throws Exception {
        when(characterService.getCharacter("Batman", "1000")).thenThrow(new CensusException());

        mockMvc.perform(get("/v1/census/characters").param("name", "Batman").param("worldId", "1000"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_GATEWAY.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("The Daybreak Games Census API did not respond."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters"));
    }

    @Test
    void getCharacterImage_returnsPngImage_whenGenderIdIsProvided() throws Exception {
        byte[] image = "image-bytes".getBytes(StandardCharsets.UTF_8);
        when(characterService.getCharacterImage("100", "1")).thenReturn(image);

        mockMvc.perform(get("/v1/census/characters/100/image").param("genderId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(image));
    }

    @Test
    void getCharacterImage_returnsPngImage_whenGenderIdIsOmitted() throws Exception {
        byte[] image = "image-bytes".getBytes(StandardCharsets.UTF_8);
        when(characterService.getCharacterImage("100", null)).thenReturn(image);

        mockMvc.perform(get("/v1/census/characters/100/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(image));

        verify(characterService).getCharacterImage("100", null);
    }

    @Test
    void getCharacterImage_returnsBadGatewayError_whenCensusIsUnreachable() throws Exception {
        when(characterService.getCharacterImage("100", null)).thenThrow(new CensusException());

        mockMvc.perform(get("/v1/census/characters/100/image"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_GATEWAY.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("The Daybreak Games Census API did not respond."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters/100/image"));
    }

    @Test
    void getCharacterImage_returnsNotFoundError_whenCharacterGenderNotFound() throws Exception {
        when(characterService.getCharacterImage("100", null)).thenThrow(new CharacterNotFoundException());

        mockMvc.perform(get("/v1/census/characters/100/image"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Character not found."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters/100/image"));
    }

    @Test
    void getCharacterImage_returnsInternalServerError_whenGenderReferenceDataIsMissing() throws Exception {
        when(characterService.getCharacterImage("100", null)).thenThrow(new MissingDataException());

        mockMvc.perform(get("/v1/census/characters/100/image"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message")
                        .value("We are missing some data for this character/league, please report this to an administrator."))
                .andExpect(jsonPath("$.path").value("/v1/census/characters/100/image"));
    }

    @Test
    void getCharacterImage_propagatesException_whenImageCannotBeRead() throws Exception {
        when(characterService.getCharacterImage("100", null)).thenThrow(new IOException("unreachable"));

        assertThatThrownBy(() -> mockMvc.perform(get("/v1/census/characters/100/image")))
                .isInstanceOf(IOException.class);
    }

    private CharacterResponse rankingCharacterResponse() {
        CharacterResponse response = new CharacterResponse();
        response.setCharacterId("100");
        response.setWorldId("1000");
        response.setName("Batman");
        response.setCombatRating(500);

        CharacterStatsResponse stats = new CharacterStatsResponse();
        stats.setHealth(10000);
        response.setStats(stats);

        return response;
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
