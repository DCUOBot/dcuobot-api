package com.dcuobot.api.config;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.character.CensusCharacter;
import com.dcuobot.api.census.dto.character.CensusCharacterItem;
import com.dcuobot.api.census.dto.character.CensusCharacterItemList;
import com.dcuobot.api.census.dto.character.CensusCharacterList;
import com.dcuobot.api.census.dto.guild.CensusGuild;
import com.dcuobot.api.census.dto.guild.CensusGuildList;
import com.dcuobot.api.census.dto.guild.CensusGuildRosterList;
import com.dcuobot.api.census.dto.status.CensusGameServerStatus;
import com.dcuobot.api.census.dto.status.CensusGameServerStatusList;
import com.dcuobot.api.character.control.CharacterService;
import com.dcuobot.api.common.sort.SortCriteriaHelpers;
import com.dcuobot.api.common.worldid.WorldIdHelpers;
import com.dcuobot.api.gamedata.entity.Alignment;
import com.dcuobot.api.gamedata.entity.Gender;
import com.dcuobot.api.gamedata.entity.GuildAlignment;
import com.dcuobot.api.gamedata.entity.MovementMode;
import com.dcuobot.api.gamedata.entity.Personality;
import com.dcuobot.api.gamedata.entity.PowerType;
import com.dcuobot.api.gamedata.repository.AlignmentRepository;
import com.dcuobot.api.gamedata.repository.AllyRepository;
import com.dcuobot.api.gamedata.repository.ArtifactRepository;
import com.dcuobot.api.gamedata.repository.GenderRepository;
import com.dcuobot.api.gamedata.repository.GuildAlignmentRepository;
import com.dcuobot.api.gamedata.repository.MovementModeRepository;
import com.dcuobot.api.gamedata.repository.PersonalityRepository;
import com.dcuobot.api.gamedata.repository.PowerTypeRepository;
import com.dcuobot.api.guild.control.GuildService;
import com.dcuobot.api.guild.repository.GuildRepository;
import com.dcuobot.api.status.control.GameServerStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies that {@link CacheConfig}'s caches actually short-circuit repeated Census calls.
 * The existing {@code *ServiceTest} classes instantiate their services directly via
 * {@code new XService(...)}, which bypasses Spring's caching proxy entirely, so they prove
 * nothing about caching behavior. This test boots a minimal Spring context (just the cache
 * config and the three cached services, with every collaborator mocked) so {@code @Cacheable}
 * is actually applied.
 */
@SpringBootTest(classes = {CacheConfig.class, CharacterService.class, GuildService.class, GameServerStatusService.class})
class CacheConfigTest {

    @MockitoBean
    private CensusClient censusClient;
    @MockitoBean
    private WorldIdHelpers worldIdHelpers;
    @MockitoBean
    private SortCriteriaHelpers sortCriteriaHelpers;
    @MockitoBean
    private ArtifactRepository artifactRepository;
    @MockitoBean
    private AllyRepository allyRepository;
    @MockitoBean
    private AlignmentRepository alignmentRepository;
    @MockitoBean
    private PowerTypeRepository powerTypeRepository;
    @MockitoBean
    private MovementModeRepository movementModeRepository;
    @MockitoBean
    private PersonalityRepository personalityRepository;
    @MockitoBean
    private GenderRepository genderRepository;
    @MockitoBean
    private GuildRepository guildRepository;
    @MockitoBean
    private GuildAlignmentRepository guildAlignmentRepository;

    @Autowired
    private CharacterService characterService;
    @Autowired
    private GuildService guildService;
    @Autowired
    private GameServerStatusService gameServerStatusService;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        lenient().when(worldIdHelpers.isValidWorldId(anyString())).thenReturn(true);
        lenient().when(sortCriteriaHelpers.isValidCharacterSortCriteria(anyString())).thenReturn(true);

        // The Spring context (and its Caffeine caches) is reused across test methods in this
        // class, so entries from one test would otherwise leak into the next.
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Test
    void getCharacter_hitsCensusOnce_forRepeatedIdenticalLookups() {
        stubCharacterSearch("Batman", "1000");

        characterService.getCharacter("Batman", "1000");
        characterService.getCharacter("Batman", "1000");

        verify(censusClient, times(1)).getCharacter("Batman", "1000", false);
    }

    @Test
    void getCharacter_treatsNameAsCaseInsensitive_forCaching() {
        stubCharacterSearch("Batman", "1000");

        characterService.getCharacter("Batman", "1000");
        characterService.getCharacter("batman", "1000");

        verify(censusClient, times(1)).getCharacter("Batman", "1000", false);
    }

    @Test
    void getCharacter_hitsCensusAgain_forADifferentWorldId() {
        stubCharacterSearch("Batman", "1000");
        stubCharacterSearch("Batman", "2000");

        characterService.getCharacter("Batman", "1000");
        characterService.getCharacter("Batman", "2000");

        verify(censusClient, times(1)).getCharacter("Batman", "1000", false);
        verify(censusClient, times(1)).getCharacter("Batman", "2000", false);
    }

    @Test
    void getCharacterRanking_hitsCensusOnce_forRepeatedIdenticalRankings() {
        stubRankingSearch("1000", "combat_rating");

        characterService.getCharacterRanking("1000", "combat_rating");
        characterService.getCharacterRanking("1000", "combat_rating");

        verify(censusClient, times(1)).getCharacterRanking("1000", "combat_rating");
    }

    @Test
    void getGuild_hitsCensusOnce_forRepeatedIdenticalLookups() {
        stubGuildSearch("Justice League", "1000");

        guildService.getGuild("Justice League", "1000");
        guildService.getGuild("Justice League", "1000");

        verify(censusClient, times(1)).getGuild("Justice League", "1000");
    }

    @Test
    void getGuild_treatsNameAsCaseInsensitive_forCaching() {
        stubGuildSearch("Justice League", "1000");

        guildService.getGuild("Justice League", "1000");
        guildService.getGuild("justice league", "1000");

        verify(censusClient, times(1)).getGuild("Justice League", "1000");
    }

    @Test
    void getGameServerStatus_hitsCensusOnce_forRepeatedCalls() {
        CensusGameServerStatus server = new CensusGameServerStatus();
        server.setName("USPC1");
        server.setLastReportedState("high");
        CensusGameServerStatusList statusList = new CensusGameServerStatusList();
        statusList.setGameServerStatusList(List.of(server));
        when(censusClient.getGameServerStatus()).thenReturn(statusList);

        gameServerStatusService.getGameServerStatus();
        gameServerStatusService.getGameServerStatus();

        verify(censusClient, times(1)).getGameServerStatus();
    }

    private void stubCharacterSearch(String name, String worldId) {
        String characterId = name + "-" + worldId;

        CensusCharacter character = new CensusCharacter();
        character.setCharacterId(characterId);
        character.setWorldId(worldId);
        character.setName(name);
        character.setAlignmentId("1");
        character.setGenderId("0");
        character.setPowerTypeId("2");
        character.setMovementModeId("3");
        character.setPersonalityId("4");

        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(character));
        when(censusClient.getCharacter(name, worldId, false)).thenReturn(characterList);

        when(alignmentRepository.findByCensusId("1")).thenReturn(Optional.of(new Alignment()));
        when(powerTypeRepository.findByCensusId("2")).thenReturn(Optional.of(new PowerType()));
        when(movementModeRepository.findByCensusId("3")).thenReturn(Optional.of(new MovementMode()));
        when(personalityRepository.findByCensusId("4")).thenReturn(Optional.of(new Personality()));
        when(genderRepository.findByCensusId("0")).thenReturn(Optional.of(new Gender()));

        CensusCharacterItemList emptyItems = new CensusCharacterItemList();
        emptyItems.setCharactersItemList(new CensusCharacterItem[0]);
        when(censusClient.getCharacterArtifacts(characterId)).thenReturn(emptyItems);
        when(censusClient.getCharacterAllies(characterId)).thenReturn(emptyItems);
    }

    private void stubRankingSearch(String worldId, String sort) {
        CensusCharacter character = new CensusCharacter();
        character.setCharacterId("100");
        character.setWorldId(worldId);
        character.setName("Batman");
        character.setGenderId("0");

        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(character));
        when(censusClient.getCharacterRanking(worldId, sort)).thenReturn(characterList);

        when(alignmentRepository.findAll()).thenReturn(List.of());
        when(powerTypeRepository.findAll()).thenReturn(List.of());
        when(movementModeRepository.findAll()).thenReturn(List.of());
        when(personalityRepository.findAll()).thenReturn(List.of());
        when(genderRepository.findByCensusId("0")).thenReturn(Optional.of(new Gender()));
    }

    private void stubGuildSearch(String name, String worldId) {
        String guildId = name + "-" + worldId;

        CensusGuild guild = new CensusGuild();
        guild.setGuildId(guildId);
        guild.setName(name);
        guild.setWorldId(worldId);
        guild.setCharacterAlignmentId("1");

        CensusGuildList guildList = new CensusGuildList();
        guildList.setGuildList(List.of(guild));
        when(censusClient.getGuild(name, worldId)).thenReturn(guildList);

        CensusGuildRosterList rosterList = new CensusGuildRosterList();
        rosterList.setGuildRosterList(List.of());
        when(censusClient.getGuildRoster(guildId)).thenReturn(rosterList);

        when(guildAlignmentRepository.findByCensusId("1")).thenReturn(Optional.of(new GuildAlignment()));
        when(guildRepository.findByCensusId(guildId)).thenReturn(Optional.empty());
    }
}
