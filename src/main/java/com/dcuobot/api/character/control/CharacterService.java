package com.dcuobot.api.character.control;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.character.*;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.character.dto.*;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.common.sort.InvalidSortCriteriaException;
import com.dcuobot.api.common.sort.SortCriteriaHelpers;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import com.dcuobot.api.common.worldid.WorldIdHelpers;
import com.dcuobot.api.gamedata.entity.*;
import com.dcuobot.api.gamedata.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

/**
 * Assembles a {@link CharacterResponse} for a DCUO character by combining live data from the
 * Census API with static reference data (alignments, power types, artifacts, etc.) resolved
 * through the gamedata repositories.
 */
@Service
@RequiredArgsConstructor
public class CharacterService {
    private final static String PAPERDOLL_URL = "https://census-proxy.dcuo.bot/files/dcuo/images/character/paperdoll";

    private final CensusClient censusClient;

    private final WorldIdHelpers worldIdHelpers;
    private final SortCriteriaHelpers sortCriteriaHelpers;

    private final ArtifactRepository artifactRepository;
    private final AllyRepository allyRepository;
    private final AlignmentRepository alignmentRepository;
    private final PowerTypeRepository powerTypeRepository;
    private final MovementModeRepository movementModeRepository;
    private final PersonalityRepository personalityRepository;
    private final GenderRepository genderRepository;

    /**
     * Looks up a character by name and world, and builds the full response including stats,
     * guild, artifacts, and allies.
     *
     * @param name    the character name to search for; searches match are exact unless the
     *                name is under 3 characters, in which case a wildcard search is used
     * @param worldId the world (server) id the character belongs to
     * @return the assembled character response
     * @throws CensusException            if the Census API is unreachable or returns malformed data
     * @throws CharacterNotFoundException if no character matches the given name/world
     * @throws MissingDataException       if reference data required to resolve the character is missing
     */
    public CharacterResponse getCharacter(String name, String worldId)
            throws InvalidWorldIdException, CensusException, CharacterNotFoundException {
        if (!worldIdHelpers.isValidWorldId(worldId)) {
            throw new InvalidWorldIdException();
        }

        CensusCharacter character = fetchCharacter(name, worldId);
        CensusCharacterGuild guild = fetchGuild(character);

        CharacterResponse response = buildBaseCharacterResponse(character);
        response.setAlignment(alignmentRepository.findByCensusId(character.getAlignmentId())
                .orElseThrow(MissingDataException::new).getName());
        response.setPowerType(powerTypeRepository.findByCensusId(character.getPowerTypeId())
                .orElseThrow(MissingDataException::new).getName());
        response.setMovementMode(movementModeRepository.findByCensusId(character.getMovementModeId())
                .orElseThrow(MissingDataException::new).getName());
        response.setPersonality(personalityRepository.findByCensusId(character.getPersonalityId())
                .orElseThrow(MissingDataException::new).getName());
        response.setImage(buildImage(character));
        response.setGuild(buildGuildResponse(guild));
        response.setArtifacts(fetchArtifacts(character.getCharacterId()));
        response.setAllies(fetchAllies(character.getCharacterId()));

        return response;
    }

    /**
     * Fetches a character's paperdoll image, falling back to a gender-based placeholder image
     * when no rendered paperdoll is available for the character.
     *
     * @param characterId the character's Census id
     * @param genderId    the character's gender id, used to resolve the fallback image; if
     *                    {@code null} or blank and a fallback is needed, the gender is looked
     *                    up from Census
     * @return the image bytes, PNG-encoded
     * @throws IOException                if the resolved image cannot be read or re-encoded
     * @throws CensusException            if the Census API is unreachable or returns malformed data
     * @throws CharacterNotFoundException if no character is returned when resolving the gender
     * @throws MissingDataException       if the resolved gender has no matching reference data
     */
    public byte[] getCharacterImage(String characterId, String genderId) throws IOException {
        BufferedImage image = null;

        try {
            image = ImageIO.read(URI.create(PAPERDOLL_URL + "/" + characterId).toURL());
        } catch (IOException ignored) {
        }

        if (image == null) {
            String gId = genderId;

            if (gId == null || gId.trim().isEmpty()) {
                CensusCharacterGenderList genderList = censusClient.getCharacterGender(characterId);

                if (genderList == null || genderList.getCharacterList() == null) {
                    throw new CensusException();
                }

                CensusCharacterGender gender = genderList.getCharacterList()
                        .stream()
                        .findFirst()
                        .orElseThrow(CharacterNotFoundException::new);

                gId = gender.getGenderId();
            }

            Gender gender = genderRepository.findByCensusId(gId).orElseThrow(MissingDataException::new);

            image = ImageIO.read(URI.create(gender.getImageUrl()).toURL());
        }

        byte[] result;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", out);
            result = out.toByteArray();
        }

        return result;
    }

    /**
     * Builds the world's character ranking, resolving each character's reference data
     * (alignment, power type, movement mode, personality) against name lookups built once
     * up front, rather than querying per character. Unlike {@link #getCharacter}, missing
     * reference data is left {@code null} instead of failing the whole ranking, and guild
     * and artifacts are omitted since the ranking endpoint doesn't return them.
     *
     * @param worldId the world (server) id to rank characters within
     * @param sort    the stat to rank characters by; must be one of the supported character
     *                sort criteria (e.g. {@code "combat_rating"}, {@code "skill_points"})
     * @throws InvalidSortCriteriaException if {@code sort} is not a supported sort criterion
     * @throws CensusException              if the Census API is unreachable or returns malformed data
     */
    public Collection<CharacterResponse> getCharacterRanking(String worldId, String sort)
            throws CensusException, InvalidSortCriteriaException {
        if (!sortCriteriaHelpers.isValidCharacterSortCriteria(sort)) {
            throw new InvalidSortCriteriaException();
        }

        CensusCharacterList characterList = censusClient.getCharacterRanking(worldId, sort);

        if (characterList == null || characterList.getCharacterList() == null) {
            throw new CensusException();
        }

        Map<String, String> alignmentNames =
                indexNamesByCensusId(alignmentRepository.findAll(), Alignment::getCensusId, Alignment::getName);
        Map<String, String> powerTypeNames =
                indexNamesByCensusId(powerTypeRepository.findAll(), PowerType::getCensusId, PowerType::getName);
        Map<String, String> movementModeNames = indexNamesByCensusId(
                movementModeRepository.findAll(), MovementMode::getCensusId, MovementMode::getName);
        Map<String, String> personalityNames = indexNamesByCensusId(
                personalityRepository.findAll(), Personality::getCensusId, Personality::getName);

        return characterList.getCharacterList()
                .stream()
                .map(character -> {
                    CharacterResponse response = buildBaseCharacterResponse(character);
                    response.setAlignment(alignmentNames.get(character.getAlignmentId()));
                    response.setPowerType(powerTypeNames.get(character.getPowerTypeId()));
                    response.setMovementMode(movementModeNames.get(character.getMovementModeId()));
                    response.setPersonality(personalityNames.get(character.getPersonalityId()));
                    response.setImage(buildImage(character));
                    return response;
                })
                .toList();
    }

    /**
     * Fetches the character matching {@code name}/{@code worldId} from Census.
     *
     * @throws CensusException            if Census is unreachable or the response is malformed
     * @throws CharacterNotFoundException if no character is returned for the query
     */
    private CensusCharacter fetchCharacter(String name, String worldId) {
        CensusCharacterList characterList = censusClient.getCharacter(name, worldId, name.length() < 3);

        if (characterList == null || characterList.getCharacterList() == null) {
            throw new CensusException();
        }

        return characterList.getCharacterList()
                .stream()
                .findFirst()
                .orElseThrow(CharacterNotFoundException::new);
    }

    /**
     * Fetches the character's guild from Census, if they belong to one.
     *
     * @return the guild, or {@code null} if the character has no guild roster entry
     * @throws CensusException if Census is unreachable or the response is malformed
     */
    private CensusCharacterGuild fetchGuild(CensusCharacter character) {
        if (character.getGuildRoster() == null || character.getGuildRoster().isEmpty()) {
            return null;
        }

        CensusCharacterGuildList characterGuildList =
                censusClient.getCharacterGuild(character.getGuildRoster().getFirst().getGuildId());

        if (characterGuildList == null || characterGuildList.getGuildList() == null) {
            throw new CensusException();
        }

        return characterGuildList.getGuildList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Fetches the character's equipped artifacts from Census and resolves each against the
     * {@link ArtifactRepository}. Items are returned sorted by equipment slot id ascending;
     * the first two entries are the base artifact slots, the entry at index 8 (or 7 if there
     * is no 9th entry) is the third/adept artifact slot, and slots {@code "34"}/{@code "35"}
     * are looked up explicitly for the fourth and fifth artifact slots.
     *
     * @throws CensusException if Census is unreachable or the response is malformed
     */
    private Collection<CharacterArtifactResponse> fetchArtifacts(String characterId) {
        CensusCharacterItemList characterArtifactsList = censusClient.getCharacterArtifacts(characterId);

        if (characterArtifactsList == null || characterArtifactsList.getCharactersItemList() == null) {
            throw new CensusException();
        }

        CensusCharacterItem[] items = characterArtifactsList.getCharactersItemList();
        Collection<CharacterArtifactResponse> artifacts = new ArrayList<>();

        itemAt(items, 0).ifPresent(item -> addArtifact(artifacts, item));
        itemAt(items, 1).ifPresent(item -> addArtifact(artifacts, item));

        itemAt(items, 8)
                .or(() -> itemAt(items, 7))
                .ifPresent(item -> addArtifact(artifacts, item));

        addArtifactBySlot(artifacts, items, "34");
        addArtifactBySlot(artifacts, items, "35");

        return artifacts;
    }

    /**
     * Finds the item equipped in {@code slotId}, if any, and resolves/adds it as an artifact.
     */
    private void addArtifactBySlot(Collection<CharacterArtifactResponse> artifacts, CensusCharacterItem[] items, String slotId) {
        Arrays.stream(items)
                .filter(item -> item.getEquipmentSlotId().equals(slotId))
                .findFirst()
                .ifPresent(item -> addArtifact(artifacts, item));
    }

    /**
     * Resolves {@code item} against the {@link ArtifactRepository} and adds it if known.
     */
    private void addArtifact(Collection<CharacterArtifactResponse> artifacts, CensusCharacterItem item) {
        artifactRepository.findByCensusId(item.getItemId())
                .ifPresent(artifact -> artifacts.add(CharacterArtifactResponse.fromEntity(artifact)));
    }

    /**
     * Fetches the character's equipped allies (the first three ally item slots) from Census
     * and resolves each against the {@link AllyRepository}.
     *
     * @throws CensusException if Census is unreachable or the response is malformed
     */
    private Collection<CharacterAllyResponse> fetchAllies(String characterId) {
        CensusCharacterItemList characterAlliesList = censusClient.getCharacterAllies(characterId);

        if (characterAlliesList == null || characterAlliesList.getCharactersItemList() == null) {
            throw new CensusException();
        }

        CensusCharacterItem[] items = characterAlliesList.getCharactersItemList();
        Collection<CharacterAllyResponse> allies = new ArrayList<>();

        itemAt(items, 0).ifPresent(item -> addAlly(allies, item));
        itemAt(items, 1).ifPresent(item -> addAlly(allies, item));
        itemAt(items, 2).ifPresent(item -> addAlly(allies, item));

        return allies;
    }

    /**
     * Resolves {@code item} against the {@link AllyRepository} and adds it if known. An ally
     * equipped in slot {@code "31"} is flagged as the active combat ally.
     */
    private void addAlly(Collection<CharacterAllyResponse> allies, CensusCharacterItem item) {
        allyRepository.findByCensusId(item.getItemId())
                .ifPresent(ally -> allies.add(CharacterAllyResponse.fromEntity(
                        ally,
                        item.getEquipmentSlotId().equalsIgnoreCase("31")
                )));
    }

    /**
     * Bounds-checked array access, returning empty instead of throwing when {@code index} is out of range.
     */
    private static Optional<CensusCharacterItem> itemAt(CensusCharacterItem[] items, int index) {
        return index < items.length && items[index] != null
                ? Optional.of(items[index])
                : Optional.empty();
    }

    /**
     * Builds the character's image response, pairing the dcuo.bot rendered image URL with a
     * gender-based fallback image.
     *
     * @throws MissingDataException if the character's gender has no matching reference data
     */
    private CharacterImageResponse buildImage(CensusCharacter character) {
        CharacterImageResponse characterImage = new CharacterImageResponse();
        characterImage.setUrl("https://dcuo.bot/api/v1/census/characters/" + character.getCharacterId() +
                "/image?genderId=" + character.getGenderId());
        characterImage.setAltUrl(genderRepository.findByCensusId(character.getGenderId())
                .orElseThrow(MissingDataException::new).getImageUrl());
        return characterImage;
    }

    /**
     * Maps a Census guild to its response DTO.
     *
     * @return the guild response, or {@code null} if {@code guild} is {@code null}
     */
    private CharacterGuildResponse buildGuildResponse(CensusCharacterGuild guild) {
        if (guild == null) {
            return null;
        }

        CharacterGuildResponse characterGuildResponse = new CharacterGuildResponse();
        characterGuildResponse.setId(guild.getGuildId());
        characterGuildResponse.setName(guild.getName());
        return characterGuildResponse;
    }

    /**
     * Maps the character's raw Census stat fields to the stats response, defaulting missing values to zero.
     */
    private CharacterStatsResponse buildStats(CensusCharacter character) {
        CharacterStatsResponse characterStats = new CharacterStatsResponse();
        characterStats.setHealth(parseIntOrZero(character.getMaxHealth()));
        characterStats.setPower(parseIntOrZero(character.getMaxPower()));
        characterStats.setDefense(parseIntOrZero(character.getDefense()));
        characterStats.setToughness(parseIntOrZero(character.getToughness()));
        characterStats.setMight(parseIntOrZero(character.getMight()));
        characterStats.setPrecision(parseIntOrZero(character.getPrecision()));
        characterStats.setRestoration(parseIntOrZero(character.getRestoration()));
        characterStats.setVitalization(parseIntOrZero(character.getVitalization()));
        characterStats.setDominance(parseIntOrZero(character.getDominance()));
        return characterStats;
    }

    /**
     * Parses {@code value} as an integer, treating {@code null} as zero.
     */
    private static int parseIntOrZero(String value) {
        return value != null ? Integer.parseInt(value) : 0;
    }

    /**
     * Builds the fields common to every character response (identity, gender label, ratings,
     * stats), shared by {@link #getCharacter} and {@link #getCharacterRanking}. Callers are
     * responsible for resolving and setting alignment, power type, movement mode, personality,
     * image, guild, artifacts, and allies, since those differ between the two use cases.
     */
    private CharacterResponse buildBaseCharacterResponse(CensusCharacter character) {
        CharacterResponse response = new CharacterResponse();
        response.setCharacterId(character.getCharacterId());
        response.setWorldId(character.getWorldId());
        response.setName(character.getName());
        response.setGender(resolveGenderLabel(character.getGenderId()));
        response.setCombatRating(parseIntOrZero(character.getCombatRating()));
        response.setPvpCombatRating(parseIntOrZero(character.getPvpCombatRating()));
        response.setSkillPoints(parseIntOrZero(character.getSkillPoints()));
        response.setStats(buildStats(character));
        return response;
    }

    /**
     * Resolves a Census gender id to its display label. Census only models two genders, with
     * {@code "0"} representing male.
     */
    private static String resolveGenderLabel(String genderId) {
        return "0".equals(genderId) ? "Male" : "Female";
    }

    /**
     * Indexes {@code items} by census id, for O(1) name lookups instead of scanning the list
     * once per character being resolved.
     */
    private static <T> Map<String, String> indexNamesByCensusId(
            List<T> items, Function<T, String> censusIdExtractor, Function<T, String> nameExtractor) {
        Map<String, String> namesByCensusId = new HashMap<>();

        for (T item : items) {
            namesByCensusId.put(censusIdExtractor.apply(item), nameExtractor.apply(item));
        }

        return namesByCensusId;
    }
}
