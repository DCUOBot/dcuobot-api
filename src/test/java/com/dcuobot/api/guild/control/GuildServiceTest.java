package com.dcuobot.api.guild.control;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.guild.CensusGuild;
import com.dcuobot.api.census.dto.guild.CensusGuildList;
import com.dcuobot.api.census.dto.guild.CensusGuildRoster;
import com.dcuobot.api.census.dto.guild.CensusGuildRosterCharacter;
import com.dcuobot.api.census.dto.guild.CensusGuildRosterList;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import com.dcuobot.api.common.worldid.WorldIdHelpers;
import com.dcuobot.api.gamedata.entity.GuildAlignment;
import com.dcuobot.api.gamedata.repository.GuildAlignmentRepository;
import com.dcuobot.api.guild.dto.GuildCharacterResponse;
import com.dcuobot.api.guild.dto.GuildResponse;
import com.dcuobot.api.guild.entity.Guild;
import com.dcuobot.api.guild.exception.GuildNotFoundException;
import com.dcuobot.api.guild.repository.GuildRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GuildServiceTest {

    @Mock
    private CensusClient censusClient;
    @Mock
    private WorldIdHelpers worldIdHelpers;
    @Mock
    private GuildRepository guildRepository;
    @Mock
    private GuildAlignmentRepository guildAlignmentRepository;

    private GuildService guildService;

    @BeforeEach
    void setUp() {
        guildService = new GuildService(censusClient, worldIdHelpers, guildRepository, guildAlignmentRepository);
        lenient().when(worldIdHelpers.isValidWorldId("1000")).thenReturn(true);
    }

    @Test
    void getGuild_throwsInvalidWorldIdException_whenWorldIdIsInvalid() {
        when(worldIdHelpers.isValidWorldId("9999")).thenReturn(false);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "9999"))
                .isInstanceOf(InvalidWorldIdException.class);
        verifyNoInteractions(censusClient);
    }

    @Test
    void getGuild_throwsCensusException_whenGuildSearchResponseIsNull() {
        when(censusClient.getGuild(anyString(), anyString())).thenReturn(null);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getGuild_throwsCensusException_whenGuildSearchListIsNull() {
        CensusGuildList guildList = new CensusGuildList();
        guildList.setGuildList(null);
        when(censusClient.getGuild(anyString(), anyString())).thenReturn(guildList);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getGuild_throwsGuildNotFoundException_whenNoGuildMatches() {
        stubGuildNotFoundInCensus();
        when(guildRepository.existsByNameAndWorldId("Justice League", "1000")).thenReturn(false);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(GuildNotFoundException.class);
    }

    @Test
    void getGuild_deletesCachedGuild_whenNoLongerFoundInCensusAndPresentInDatabase() {
        stubGuildNotFoundInCensus();
        when(guildRepository.existsByNameAndWorldId("Justice League", "1000")).thenReturn(true);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(GuildNotFoundException.class);

        verify(guildRepository).deleteByNameAndWorldId("Justice League", "1000");
    }

    @Test
    void getGuild_doesNotDeleteCachedGuild_whenNoLongerFoundInCensusAndAbsentFromDatabase() {
        stubGuildNotFoundInCensus();
        when(guildRepository.existsByNameAndWorldId("Justice League", "1000")).thenReturn(false);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(GuildNotFoundException.class);

        verify(guildRepository, never()).deleteByNameAndWorldId(anyString(), anyString());
    }

    @Test
    void getGuild_throwsCensusException_whenRosterResponseIsNull() {
        stubGuildSearch(defaultGuild());
        when(censusClient.getGuildRoster("500")).thenReturn(null);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getGuild_throwsCensusException_whenRosterListIsNull() {
        stubGuildSearch(defaultGuild());
        CensusGuildRosterList rosterList = new CensusGuildRosterList();
        rosterList.setGuildRosterList(null);
        when(censusClient.getGuildRoster("500")).thenReturn(rosterList);

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getGuild_throwsMissingDataException_whenAlignmentIsUnknown() {
        stubGuildSearch(defaultGuild());
        stubRoster();
        when(guildAlignmentRepository.findByCensusId("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> guildService.getGuild("Justice League", "1000"))
                .isInstanceOf(MissingDataException.class);
    }

    @Test
    void getGuild_resolvesNeutralAlignment_whenCensusAlignmentIdIsNull() {
        CensusGuild guild = defaultGuild();
        guild.setCharacterAlignmentId(null);
        stubGuildSearch(guild);
        stubRoster();
        stubAlignment("0", "Neutral");
        stubExistingGuild(Optional.empty());

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getAlignment()).isEqualTo("Neutral");
        verify(guildAlignmentRepository).findByCensusId("0");
        verify(guildAlignmentRepository, never()).findByCensusId("1");
    }

    @Test
    void getGuild_returnsFullyPopulatedResponse_withAveragedStats() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());

        CensusGuildRosterCharacter batman = rosterCharacter("1", "Batman", "100", "500", "400");
        CensusGuildRosterCharacter robin = rosterCharacter("2", "Robin", "200", "300", "200");
        stubRoster(rosterEntry("1000", "1", batman), rosterEntry("1000", "2", robin));

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getGuildId()).isEqualTo("500");
        assertThat(response.getWorldId()).isEqualTo("1000");
        assertThat(response.getName()).isEqualTo("Justice League");
        assertThat(response.getAlignment()).isEqualTo("Good");
        assertThat(response.getMemberCount()).isEqualTo(2);
        assertThat(response.getAverageSkillPoints()).isEqualTo(150.0);
        assertThat(response.getAverageCombatRating()).isEqualTo(400.0);
        assertThat(response.getAveragePvpCombatRating()).isEqualTo(300.0);

        assertThat(response.getCharacters())
                .extracting(GuildCharacterResponse::getCharacterId, GuildCharacterResponse::getName, GuildCharacterResponse::getRank,
                        GuildCharacterResponse::getSkillPoints, GuildCharacterResponse::getCombatRating, GuildCharacterResponse::getPvpCombatRating)
                .containsExactly(
                        tuple("1", "Batman", 1, 100, 500, 400),
                        tuple("2", "Robin", 2, 200, 300, 200));

        ArgumentCaptor<Guild> guildCaptor = ArgumentCaptor.forClass(Guild.class);
        verify(guildRepository).save(guildCaptor.capture());
        Guild saved = guildCaptor.getValue();
        assertThat(saved.getCensusId()).isEqualTo("500");
        assertThat(saved.getName()).isEqualTo("Justice League");
        assertThat(saved.getWorldId()).isEqualTo("1000");
        assertThat(saved.getMemberCount()).isEqualTo(2);
        assertThat(saved.getAverageSkillPoints()).isEqualTo(150.0);
        assertThat(saved.getAverageCombatRating()).isEqualTo(400.0);
        assertThat(saved.getAveragePvpCombatRating()).isEqualTo(300.0);
    }

    @Test
    void getGuild_updatesExistingGuild_insteadOfCreatingANewOne() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubRoster(rosterEntry("1000", "1", rosterCharacter("1", "Batman", "100", "500", "400")));

        Guild existingGuild = new Guild();
        existingGuild.setCensusId("500");
        existingGuild.setMemberCount(99);
        stubExistingGuild(Optional.of(existingGuild));

        guildService.getGuild("Justice League", "1000");

        ArgumentCaptor<Guild> guildCaptor = ArgumentCaptor.forClass(Guild.class);
        verify(guildRepository).save(guildCaptor.capture());
        assertThat(guildCaptor.getValue()).isSameAs(existingGuild);
        assertThat(guildCaptor.getValue().getMemberCount()).isEqualTo(1);
    }

    @Test
    void getGuild_excludesRosterEntries_withNoResolvedCharacter() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());

        CensusGuildRoster missingCharacter = rosterEntry("1000", "1", null);
        CensusGuildRoster batman = rosterEntry("1000", "2", rosterCharacter("1", "Batman", "100", "500", "400"));
        stubRoster(missingCharacter, batman);

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getMemberCount()).isEqualTo(1);
        assertThat(response.getCharacters()).extracting(GuildCharacterResponse::getCharacterId).containsExactly("1");
        assertThat(response.getAverageSkillPoints()).isEqualTo(100.0);
    }

    @Test
    void getGuild_dedupesRosterEntries_byCharacterId_keepingTheFirstOccurrence() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());

        CensusGuildRosterCharacter batman = rosterCharacter("1", "Batman", "100", "500", "400");
        CensusGuildRoster first = rosterEntry("1000", "1", batman);
        CensusGuildRoster duplicate = rosterEntry("1000", "2", batman);
        stubRoster(first, duplicate);

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getMemberCount()).isEqualTo(1);
        assertThat(response.getCharacters()).hasSize(1);
        assertThat(response.getCharacters().getFirst().getRank()).isEqualTo(1);
        assertThat(response.getAverageSkillPoints()).isEqualTo(100.0);
    }

    @Test
    void getGuild_defaultsCharacterFieldsToZero_whenCensusFieldsAreNull() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());

        CensusGuildRosterCharacter character = rosterCharacter("1", "Batman", null, null, null);
        CensusGuildRoster roster = rosterEntry("1000", null, character);
        stubRoster(roster);

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getCharacters()).singleElement().satisfies(c -> {
            assertThat(c.getRank()).isZero();
            assertThat(c.getSkillPoints()).isZero();
            assertThat(c.getCombatRating()).isZero();
            assertThat(c.getPvpCombatRating()).isZero();
        });
        assertThat(response.getAverageSkillPoints()).isZero();
        assertThat(response.getAverageCombatRating()).isZero();
        assertThat(response.getAveragePvpCombatRating()).isZero();
    }

    @Test
    void getGuild_returnsZeroAveragesAndEmptyRoster_whenGuildHasNoMembers() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());
        stubRoster();

        GuildResponse response = guildService.getGuild("Justice League", "1000");

        assertThat(response.getMemberCount()).isZero();
        assertThat(response.getAverageSkillPoints()).isZero();
        assertThat(response.getAverageCombatRating()).isZero();
        assertThat(response.getAveragePvpCombatRating()).isZero();
        assertThat(response.getCharacters()).isEmpty();
    }

    @Test
    void getGuild_searchesCensusByNameAndWorldId() {
        stubGuildSearch(defaultGuild());
        stubAlignment("1", "Good");
        stubExistingGuild(Optional.empty());
        stubRoster();

        guildService.getGuild("Justice League", "1000");

        verify(censusClient, times(1)).getGuild("Justice League", "1000");
        verify(censusClient, times(1)).getGuildRoster("500");
    }

    @Test
    void getGuildRanking_queriesByWorldId_whenWorldIdIsProvided() {
        when(guildRepository.findAllByWorldIdAndMemberCountGreaterThanEqual(any(), eq("1000"), eq(20)))
                .thenReturn(new PageImpl<>(List.of()));

        guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, "1000");

        verify(guildRepository).findAllByWorldIdAndMemberCountGreaterThanEqual(any(), eq("1000"), eq(20));
        verify(guildRepository, never()).findAllByMemberCountGreaterThanEqual(any(), anyInt());
    }

    @Test
    void getGuildRanking_queriesAllWorlds_whenWorldIdIsNull() {
        when(guildRepository.findAllByMemberCountGreaterThanEqual(any(), eq(20)))
                .thenReturn(new PageImpl<>(List.of()));

        guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, null);

        verify(guildRepository).findAllByMemberCountGreaterThanEqual(any(), eq(20));
        verify(guildRepository, never()).findAllByWorldIdAndMemberCountGreaterThanEqual(any(), anyString(), anyInt());
    }

    @Test
    void getGuildRanking_sortsByGivenFieldThenName_inTheRequestedDirection() {
        when(guildRepository.findAllByMemberCountGreaterThanEqual(any(), eq(20)))
                .thenReturn(new PageImpl<>(List.of()));

        guildService.getGuildRanking("averageCombatRating", Sort.Direction.ASC, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(guildRepository).findAllByMemberCountGreaterThanEqual(pageableCaptor.capture(), eq(20));

        Pageable pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(100);
        assertThat(pageable.getSort())
                .extracting(Sort.Order::getProperty, Sort.Order::getDirection)
                .containsExactly(
                        tuple("averageCombatRating", Sort.Direction.ASC),
                        tuple("name", Sort.Direction.ASC));
    }

    @Test
    void getGuildRanking_sortsDescending_whenDirectionIsDesc() {
        when(guildRepository.findAllByMemberCountGreaterThanEqual(any(), eq(20)))
                .thenReturn(new PageImpl<>(List.of()));

        guildService.getGuildRanking("averageSkillPoints", Sort.Direction.DESC, null);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(guildRepository).findAllByMemberCountGreaterThanEqual(pageableCaptor.capture(), eq(20));

        assertThat(pageableCaptor.getValue().getSort())
                .extracting(Sort.Order::getProperty, Sort.Order::getDirection)
                .containsExactly(
                        tuple("averageSkillPoints", Sort.Direction.DESC),
                        tuple("name", Sort.Direction.DESC));
    }

    @Test
    void getGuildRanking_mapsEachGuildInThePage() {
        Guild justiceLeague = rankedGuild("500", "Justice League", "1000", "Good", 30, 150.0, 400.0, 300.0);
        Guild teenTitans = rankedGuild("600", "Teen Titans", "2000", "Neutral", 25, 100.0, 350.0, 250.0);
        when(guildRepository.findAllByMemberCountGreaterThanEqual(any(), eq(20)))
                .thenReturn(new PageImpl<>(List.of(justiceLeague, teenTitans)));

        List<GuildResponse> responses = guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, null);

        assertThat(responses)
                .extracting(GuildResponse::getGuildId, GuildResponse::getName, GuildResponse::getWorldId,
                        GuildResponse::getAlignment, GuildResponse::getMemberCount, GuildResponse::getAverageSkillPoints,
                        GuildResponse::getAverageCombatRating, GuildResponse::getAveragePvpCombatRating)
                .containsExactly(
                        tuple("500", "Justice League", "1000", "Good", 30, 150.0, 400.0, 300.0),
                        tuple("600", "Teen Titans", "2000", "Neutral", 25, 100.0, 350.0, 250.0));
    }

    @Test
    void getGuildRanking_returnsEmptyList_whenNoGuildsMatch() {
        when(guildRepository.findAllByMemberCountGreaterThanEqual(any(), eq(20)))
                .thenReturn(new PageImpl<>(List.of()));

        List<GuildResponse> responses = guildService.getGuildRanking("averageCombatRating", Sort.Direction.DESC, null);

        assertThat(responses).isEmpty();
    }

    private Guild rankedGuild(String censusId, String name, String worldId, String alignmentName, int memberCount,
                              double averageSkillPoints, double averageCombatRating, double averagePvpCombatRating) {
        GuildAlignment alignment = new GuildAlignment();
        alignment.setName(alignmentName);

        Guild guild = new Guild();
        guild.setCensusId(censusId);
        guild.setName(name);
        guild.setWorldId(worldId);
        guild.setAlignment(alignment);
        guild.setMemberCount(memberCount);
        guild.setAverageSkillPoints(averageSkillPoints);
        guild.setAverageCombatRating(averageCombatRating);
        guild.setAveragePvpCombatRating(averagePvpCombatRating);
        return guild;
    }

    private void stubGuildSearch(CensusGuild guild) {
        CensusGuildList guildList = new CensusGuildList();
        guildList.setGuildList(List.of(guild));
        when(censusClient.getGuild(guild.getName(), guild.getWorldId())).thenReturn(guildList);
    }

    private void stubGuildNotFoundInCensus() {
        CensusGuildList guildList = new CensusGuildList();
        guildList.setGuildList(List.of());
        when(censusClient.getGuild(anyString(), anyString())).thenReturn(guildList);
    }

    private void stubRoster(CensusGuildRoster... entries) {
        CensusGuildRosterList rosterList = new CensusGuildRosterList();
        rosterList.setGuildRosterList(List.of(entries));
        when(censusClient.getGuildRoster("500")).thenReturn(rosterList);
    }

    private void stubAlignment(String censusId, String name) {
        GuildAlignment alignment = new GuildAlignment();
        alignment.setCensusId(censusId);
        alignment.setName(name);
        when(guildAlignmentRepository.findByCensusId(censusId)).thenReturn(Optional.of(alignment));
    }

    private void stubExistingGuild(Optional<Guild> existing) {
        when(guildRepository.findByCensusId("500")).thenReturn(existing);
    }

    private CensusGuild defaultGuild() {
        CensusGuild guild = new CensusGuild();
        guild.setGuildId("500");
        guild.setName("Justice League");
        guild.setWorldId("1000");
        guild.setCharacterAlignmentId("1");
        return guild;
    }

    private CensusGuildRosterCharacter rosterCharacter(
            String characterId, String name, String skillPoints, String combatRating, String pvpCombatRating) {
        CensusGuildRosterCharacter character = new CensusGuildRosterCharacter();
        character.setCharacterId(characterId);
        character.setName(name);
        character.setSkillPoints(skillPoints);
        character.setCombatRating(combatRating);
        character.setPvpCombatRating(pvpCombatRating);
        return character;
    }

    private CensusGuildRoster rosterEntry(String worldId, String rank, CensusGuildRosterCharacter character) {
        CensusGuildRoster roster = new CensusGuildRoster();
        roster.setWorldId(worldId);
        roster.setCharacterId(character != null ? character.getCharacterId() : null);
        roster.setRank(rank);
        roster.setGuildRosterCharacter(character);
        return roster;
    }
}
