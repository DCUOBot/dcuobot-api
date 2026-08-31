package com.dcuobot.api.guild.api;

import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.common.sort.InvalidSortCriteriaException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import com.dcuobot.api.guild.control.GuildService;
import com.dcuobot.api.guild.dto.GuildCharacterResponse;
import com.dcuobot.api.guild.dto.GuildResponse;
import com.dcuobot.api.guild.exception.GuildNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuildApi.class)
class GuildApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuildService guildService;

    @Test
    void getGuilds_returnsGuildResponseAsJson_whenNameAndWorldIdAreProvided() throws Exception {
        when(guildService.getGuild("Justice League", "1000")).thenReturn(fullGuildResponse());

        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League").param("worldId", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.guild_id").value("500"))
                .andExpect(jsonPath("$.world_id").value("1000"))
                .andExpect(jsonPath("$.name").value("Justice League"))
                .andExpect(jsonPath("$.alignment").value("Good"))
                .andExpect(jsonPath("$.member_count").value(2))
                .andExpect(jsonPath("$.average_skill_points").value(150.0))
                .andExpect(jsonPath("$.average_combat_rating").value(400.0))
                .andExpect(jsonPath("$.average_pvp_combat_rating").value(300.0))
                .andExpect(jsonPath("$.characters[0].character_id").value("1"))
                .andExpect(jsonPath("$.characters[0].world_id").value("1000"))
                .andExpect(jsonPath("$.characters[0].rank").value(1))
                .andExpect(jsonPath("$.characters[0].name").value("Batman"))
                .andExpect(jsonPath("$.characters[0].skill_points").value(100))
                .andExpect(jsonPath("$.characters[0].combat_rating").value(500))
                .andExpect(jsonPath("$.characters[0].pvp_combat_rating").value(400));
    }

    @Test
    void getGuilds_returnsBadRequestError_whenNameIsProvidedWithoutWorldIdOrSort() throws Exception {
        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));

        verifyNoInteractions(guildService);
    }

    @Test
    void getGuilds_returnsBadRequestError_whenWorldIdIsProvidedWithoutNameOrSort() throws Exception {
        mockMvc.perform(get("/v1/census/guilds").param("worldId", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));

        verifyNoInteractions(guildService);
    }

    @Test
    void getGuilds_returnsBadRequestError_whenNoParamsProvided() throws Exception {
        mockMvc.perform(get("/v1/census/guilds"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Either name and worldId or sort must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));

        verifyNoInteractions(guildService);
    }

    @Test
    void getGuilds_returnsBadRequestError_whenSortIsProvidedWithoutSortDirection() throws Exception {
        mockMvc.perform(get("/v1/census/guilds").param("sort", "averageCombatRating"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("sortDirection must be provided."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));

        verifyNoInteractions(guildService);
    }

    @Test
    void getGuilds_returnsRanking_whenSortAndSortDirectionAreProvided() throws Exception {
        when(guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, null))
                .thenReturn(List.of(rankingGuildResponse()));

        mockMvc.perform(get("/v1/census/guilds").param("sort", "averageCombatRating").param("sortDirection", "DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].guild_id").value("500"))
                .andExpect(jsonPath("$[0].world_id").value("1000"))
                .andExpect(jsonPath("$[0].name").value("Justice League"))
                .andExpect(jsonPath("$[0].alignment").value("Good"))
                .andExpect(jsonPath("$[0].member_count").value(30))
                .andExpect(jsonPath("$[0].average_combat_rating").value(400.0));

        verify(guildService, never()).getGuild(anyString(), anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ASC", "DESC"})
    void getGuilds_passesSortDirectionThrough(String sortDirection) throws Exception {
        when(guildService.getGuildRanking("averageCombatRating", Sort.Direction.fromString(sortDirection), null))
                .thenReturn(List.of());

        mockMvc.perform(get("/v1/census/guilds").param("sort", "averageCombatRating").param("sortDirection", sortDirection))
                .andExpect(status().isOk());

        verify(guildService).getGuildRanking("averageCombatRating", Sort.Direction.fromString(sortDirection), null);
    }

    @Test
    void getGuilds_passesWorldIdThrough_whenWorldIdIsProvidedWithSortAndSortDirection() throws Exception {
        when(guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, "1000")).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/guilds")
                        .param("worldId", "1000")
                        .param("sort", "averageCombatRating")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk());

        verify(guildService).getGuildRanking("averageCombatRating", Sort.Direction.DESC, "1000");
    }

    @Test
    void getGuilds_ignoresNameParam_whenWorldIdIsMissingButSortAndSortDirectionAreProvided() throws Exception {
        when(guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, null)).thenReturn(List.of());

        mockMvc.perform(get("/v1/census/guilds")
                        .param("name", "Justice League")
                        .param("sort", "averageCombatRating")
                        .param("sortDirection", "DESC"))
                .andExpect(status().isOk());

        verify(guildService, never()).getGuild(anyString(), anyString());
        verify(guildService).getGuildRanking("averageCombatRating", Sort.Direction.DESC, null);
    }

    @Test
    void getGuilds_returnsBadRequestError_whenSortCriteriaIsInvalid() throws Exception {
        when(guildService.getGuildRanking("bogus", Sort.Direction.DESC, null)).thenThrow(new InvalidSortCriteriaException());

        mockMvc.perform(get("/v1/census/guilds").param("sort", "bogus").param("sortDirection", "DESC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid sort criteria."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));
    }

    @Test
    void getGuilds_returnsNotFoundError_whenGuildNotFound() throws Exception {
        when(guildService.getGuild("Justice League", "1000")).thenThrow(new GuildNotFoundException());

        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League").param("worldId", "1000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("League not found."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));
    }

    @Test
    void getGuilds_returnsBadRequestError_whenWorldIdIsInvalid() throws Exception {
        when(guildService.getGuild("Justice League", "9999")).thenThrow(new InvalidWorldIdException());

        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League").param("worldId", "9999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Invalid world id."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));
    }

    @Test
    void getGuilds_returnsBadGatewayError_whenCensusIsUnreachable() throws Exception {
        when(guildService.getGuild("Justice League", "1000")).thenThrow(new CensusException());

        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League").param("worldId", "1000"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_GATEWAY.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.BAD_GATEWAY.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("The Daybreak Games Census API did not respond."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));
    }

    @Test
    void getGuilds_returnsInternalServerError_whenAlignmentReferenceDataIsMissing() throws Exception {
        when(guildService.getGuild("Justice League", "1000")).thenThrow(new MissingDataException());

        mockMvc.perform(get("/v1/census/guilds").param("name", "Justice League").param("worldId", "1000"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.error").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message")
                        .value("We are missing some data for this character/league, please report this to an administrator."))
                .andExpect(jsonPath("$.path").value("/v1/census/guilds"));
    }

    private GuildResponse rankingGuildResponse() {
        GuildResponse response = new GuildResponse();
        response.setGuildId("500");
        response.setWorldId("1000");
        response.setName("Justice League");
        response.setAlignment("Good");
        response.setMemberCount(30);
        response.setAverageSkillPoints(150.0);
        response.setAverageCombatRating(400.0);
        response.setAveragePvpCombatRating(300.0);
        return response;
    }

    private GuildResponse fullGuildResponse() {
        GuildResponse response = new GuildResponse();
        response.setGuildId("500");
        response.setWorldId("1000");
        response.setName("Justice League");
        response.setAlignment("Good");
        response.setMemberCount(2);
        response.setAverageSkillPoints(150.0);
        response.setAverageCombatRating(400.0);
        response.setAveragePvpCombatRating(300.0);

        GuildCharacterResponse batman = new GuildCharacterResponse();
        batman.setCharacterId("1");
        batman.setWorldId("1000");
        batman.setRank(1);
        batman.setName("Batman");
        batman.setSkillPoints(100);
        batman.setCombatRating(500);
        batman.setPvpCombatRating(400);

        GuildCharacterResponse robin = new GuildCharacterResponse();
        robin.setCharacterId("2");
        robin.setWorldId("1000");
        robin.setRank(2);
        robin.setName("Robin");
        robin.setSkillPoints(200);
        robin.setCombatRating(300);
        robin.setPvpCombatRating(200);

        response.setCharacters(List.of(batman, robin));

        return response;
    }
}
