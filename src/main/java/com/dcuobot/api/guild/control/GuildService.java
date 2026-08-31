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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuildService {
    private static final int MIN_MEMBER_COUNT = 20;
    private static final int PAGE_SIZE = 100;
    private static final String SECONDARY_SORT = "name";

    private final CensusClient censusClient;

    private final WorldIdHelpers worldIdHelpers;

    private final GuildRepository guildRepository;
    private final GuildAlignmentRepository guildAlignmentRepository;

    /**
     * Looks up a guild by name and world and builds the full response including averaged
     * roster stats and the deduped member list. If the guild no longer exists in Census, any
     * cached row for it is deleted so disbanded/renamed guilds don't linger in the database.
     *
     * @throws CensusException        if the Census API is unreachable or returns malformed data
     * @throws GuildNotFoundException if no guild matches the given name/world
     * @throws MissingDataException   if the guild's alignment has no matching reference data
     */
    @Transactional
    public GuildResponse getGuild(String name, String worldId)
            throws InvalidWorldIdException, CensusException, GuildNotFoundException, MissingDataException {
        if (!worldIdHelpers.isValidWorldId(worldId)) {
            throw new InvalidWorldIdException();
        }

        CensusGuild censusGuild = fetchGuild(name, worldId);
        List<CensusGuildRoster> roster = dedupeRoster(fetchRoster(censusGuild.getGuildId()));
        GuildAlignment alignment = resolveAlignment(censusGuild);

        double avgSkillPoints = round(averageStat(roster, CensusGuildRosterCharacter::getSkillPoints));
        double avgCombatRating = round(averageStat(roster, CensusGuildRosterCharacter::getCombatRating));
        double avgPvpCombatRating = round(averageStat(roster, CensusGuildRosterCharacter::getPvpCombatRating));

        saveGuild(censusGuild, alignment, roster.size(), avgSkillPoints, avgCombatRating, avgPvpCombatRating);

        return buildResponse(censusGuild, alignment, roster, avgSkillPoints, avgCombatRating, avgPvpCombatRating);
    }

    /**
     * Ranks guilds with at least {@value MIN_MEMBER_COUNT} members by {@code sort}, optionally
     * restricted to a single world, breaking ties by name.
     *
     * @param sort          the guild field to rank by
     * @param sortDirection the direction to rank in
     * @param worldId       the world (server) id to restrict the ranking to, or {@code null} for all worlds
     */
    public List<GuildResponse> getGuildRanking(String sort, Sort.Direction sortDirection, String worldId) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(sortDirection, sort, SECONDARY_SORT));

        Page<Guild> guilds = worldId != null
                ? guildRepository.findAllByWorldIdAndMemberCountGreaterThanEqual(pageable, worldId, MIN_MEMBER_COUNT)
                : guildRepository.findAllByMemberCountGreaterThanEqual(pageable, MIN_MEMBER_COUNT);

        return guilds.stream().map(GuildResponse::fromEntity).collect(Collectors.toList());
    }

    /**
     * Fetches the guild matching {@code name}/{@code worldId} from Census. When Census returns
     * no match, deletes any cached row for that name/world before failing, since a miss here
     * means the guild was disbanded or renamed and its cached row is now stale.
     *
     * @throws CensusException        if Census is unreachable or the response is malformed
     * @throws GuildNotFoundException if no guild is returned for the query
     */
    private CensusGuild fetchGuild(String name, String worldId) {
        CensusGuildList guildList = censusClient.getGuild(name, worldId);

        if (guildList == null || guildList.getGuildList() == null) {
            throw new CensusException();
        }

        CensusGuild guild = guildList.getGuildList()
                .stream()
                .findFirst()
                .orElse(null);

        if (guild == null) {
            if (guildRepository.existsByNameAndWorldId(name, worldId)) {
                guildRepository.deleteByNameAndWorldId(name, worldId);
            }

            throw new GuildNotFoundException();
        }

        return guild;
    }

    /**
     * Fetches a guild's roster from Census.
     *
     * @throws CensusException if Census is unreachable or the response is malformed
     */
    private List<CensusGuildRoster> fetchRoster(String guildId) {
        CensusGuildRosterList rosterList = censusClient.getGuildRoster(guildId);

        if (rosterList == null || rosterList.getGuildRosterList() == null) {
            throw new CensusException();
        }

        return rosterList.getGuildRosterList();
    }

    /**
     * Filters out roster entries with no resolved character, and duplicate character ids,
     * keeping the first occurrence of each character.
     */
    private List<CensusGuildRoster> dedupeRoster(List<CensusGuildRoster> roster) {
        Set<String> seenCharacterIds = new HashSet<>();
        List<CensusGuildRoster> deduped = new ArrayList<>();

        for (CensusGuildRoster entry : roster) {
            if (entry.getGuildRosterCharacter() == null) {
                continue;
            }

            if (seenCharacterIds.add(entry.getGuildRosterCharacter().getCharacterId())) {
                deduped.add(entry);
            }
        }

        return deduped;
    }

    /**
     * Resolves a guild's alignment reference data, treating a missing Census alignment id as
     * the neutral/unset alignment ({@code "0"}).
     *
     * @throws MissingDataException if the resolved alignment id has no matching reference data
     */
    private GuildAlignment resolveAlignment(CensusGuild censusGuild) {
        String alignmentId = censusGuild.getCharacterAlignmentId() != null
                ? censusGuild.getCharacterAlignmentId() : "0";

        return guildAlignmentRepository.findByCensusId(alignmentId).orElseThrow(MissingDataException::new);
    }

    /**
     * Averages a numeric stat across the roster, treating missing values as zero and an empty
     * roster as an average of zero.
     */
    private double averageStat(List<CensusGuildRoster> roster, Function<CensusGuildRosterCharacter, String> statExtractor) {
        return roster.stream()
                .mapToInt(entry -> parseIntOrZero(statExtractor.apply(entry.getGuildRosterCharacter())))
                .average()
                .orElse(0);
    }

    /**
     * Upserts the guild's cached stats, keyed by Census guild id.
     */
    private void saveGuild(CensusGuild censusGuild, GuildAlignment alignment, int memberCount,
                           double avgSkillPoints, double avgCombatRating, double avgPvpCombatRating) {
        Guild guild = guildRepository.findByCensusId(censusGuild.getGuildId()).orElseGet(Guild::new);

        guild.setCensusId(censusGuild.getGuildId());
        guild.setAlignment(alignment);
        guild.setWorldId(censusGuild.getWorldId());
        guild.setName(censusGuild.getName());
        guild.setMemberCount(memberCount);
        guild.setAverageSkillPoints(avgSkillPoints);
        guild.setAverageCombatRating(avgCombatRating);
        guild.setAveragePvpCombatRating(avgPvpCombatRating);

        guildRepository.save(guild);
    }

    private GuildResponse buildResponse(CensusGuild censusGuild, GuildAlignment alignment, List<CensusGuildRoster> roster,
                                        double avgSkillPoints, double avgCombatRating, double avgPvpCombatRating) {
        GuildResponse response = new GuildResponse();
        response.setGuildId(censusGuild.getGuildId());
        response.setWorldId(censusGuild.getWorldId());
        response.setName(censusGuild.getName());
        response.setAlignment(alignment.getName());
        response.setMemberCount(roster.size());
        response.setAverageSkillPoints(avgSkillPoints);
        response.setAverageCombatRating(avgCombatRating);
        response.setAveragePvpCombatRating(avgPvpCombatRating);
        response.setCharacters(roster.stream().map(this::buildCharacterResponse).collect(Collectors.toList()));
        return response;
    }

    private GuildCharacterResponse buildCharacterResponse(CensusGuildRoster roster) {
        CensusGuildRosterCharacter character = roster.getGuildRosterCharacter();

        GuildCharacterResponse response = new GuildCharacterResponse();
        response.setCharacterId(roster.getCharacterId());
        response.setWorldId(roster.getWorldId());
        response.setRank(parseIntOrZero(roster.getRank()));
        response.setName(character.getName());
        response.setSkillPoints(parseIntOrZero(character.getSkillPoints()));
        response.setCombatRating(parseIntOrZero(character.getCombatRating()));
        response.setPvpCombatRating(parseIntOrZero(character.getPvpCombatRating()));
        return response;
    }

    /**
     * Parses {@code value} as an integer, treating {@code null} as zero.
     */
    private static int parseIntOrZero(String value) {
        return value != null ? Integer.parseInt(value) : 0;
    }

    private double round(double value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
