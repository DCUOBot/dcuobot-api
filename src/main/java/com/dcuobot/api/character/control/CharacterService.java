package com.dcuobot.api.character.control;

import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.dto.character.*;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.character.dto.*;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.gamedata.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Assembles a {@link CharacterResponse} for a DCUO character by combining live data from the
 * Census API with static reference data (alignments, power types, artifacts, etc.) resolved
 * through the gamedata repositories.
 */
@Service
@RequiredArgsConstructor
public class CharacterService {
    private final CensusClient censusClient;

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
     * @throws CensusException             if the Census API is unreachable or returns malformed data
     * @throws CharacterNotFoundException   if no character matches the given name/world
     * @throws MissingDataException         if reference data required to resolve the character is missing
     */
    public CharacterResponse getCharacter(String name, String worldId)
            throws CensusException, CharacterNotFoundException {
        CensusCharacter character = fetchCharacter(name, worldId);
        CensusCharacterGuild guild = fetchGuild(character);

        CharacterResponse response = new CharacterResponse();
        response.setCharacterId(character.getCharacterId());
        response.setWorldId(character.getWorldId());
        response.setName(character.getName());
        response.setAlignment(alignmentRepository.findByCensusId(character.getAlignmentId())
                .orElseThrow(MissingDataException::new).getName());
        response.setGender(character.getGenderId().equals("0") ? "Male" : "Female");
        response.setPowerType(powerTypeRepository.findByCensusId(character.getPowerTypeId())
                .orElseThrow(MissingDataException::new).getName());
        response.setMovementMode(movementModeRepository.findByCensusId(character.getMovementModeId())
                .orElseThrow(MissingDataException::new).getName());
        response.setPersonality(personalityRepository.findByCensusId(character.getPersonalityId())
                .orElseThrow(MissingDataException::new).getName());
        response.setImage(buildImage(character));
        response.setCombatRating(parseIntOrZero(character.getCombatRating()));
        response.setPvpCombatRating(parseIntOrZero(character.getPvpCombatRating()));
        response.setSkillPoints(parseIntOrZero(character.getSkillPoints()));
        response.setStats(buildStats(character));
        response.setGuild(buildGuildResponse(guild));
        response.setArtifacts(fetchArtifacts(character.getCharacterId()));
        response.setAllies(fetchAllies(character.getCharacterId()));

        return response;
    }

    /**
     * Fetches the character matching {@code name}/{@code worldId} from Census.
     *
     * @throws CensusException           if Census is unreachable or the response is malformed
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

    /** Finds the item equipped in {@code slotId}, if any, and resolves/adds it as an artifact. */
    private void addArtifactBySlot(Collection<CharacterArtifactResponse> artifacts, CensusCharacterItem[] items, String slotId) {
        Arrays.stream(items)
                .filter(item -> item.getEquipmentSlotId().equals(slotId))
                .findFirst()
                .ifPresent(item -> addArtifact(artifacts, item));
    }

    /** Resolves {@code item} against the {@link ArtifactRepository} and adds it if known. */
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

    /** Bounds-checked array access, returning empty instead of throwing when {@code index} is out of range. */
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

    /** Maps the character's raw Census stat fields to the stats response, defaulting missing values to zero. */
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

    /** Parses {@code value} as an integer, treating {@code null} as zero. */
    private static int parseIntOrZero(String value) {
        return value != null ? Integer.parseInt(value) : 0;
    }
}
