package com.dcuobot.api.gamedata.control;

import com.dcuobot.api.gamedata.dto.*;
import com.dcuobot.api.gamedata.entity.*;
import com.dcuobot.api.gamedata.repository.*;
import com.dcuobot.api.gamedata.resource.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Reads and seeds the game data reference tables (alignments, allies,
 * artifacts, genders, guild alignments, movement modes, personalities, power
 * types).
 * <p>
 * Each {@code get*} method returns the entries currently persisted in the
 * corresponding repository, mapped to their response DTO.
 * <p>
 * Each {@code loadDefault*} method seeds a repository from its bundled
 * classpath JSON file. These methods are idempotent: entries whose census ID
 * already exists in the corresponding repository are skipped, so they are
 * safe to call repeatedly (e.g., on every application startup).
 */
@Service
@RequiredArgsConstructor
public class GameDataService {
    private final static Logger LOGGER = LoggerFactory.getLogger(GameDataService.class);

    private final AlignmentRepository alignmentRepository;
    private final AllyRepository allyRepository;
    private final ArtifactRepository artifactRepository;
    private final GenderRepository genderRepository;
    private final GuildAlignmentRepository guildAlignmentRepository;
    private final MovementModeRepository movementModeRepository;
    private final PersonalityRepository personalityRepository;
    private final PowerTypeRepository powerTypeRepository;

    private final ObjectMapper objectMapper;

    @Value("classpath:gamedata/alignments.json")
    private Resource alignmentsJson;

    @Value("classpath:gamedata/allies.json")
    private Resource alliesJson;

    @Value("classpath:gamedata/artifacts.json")
    private Resource artifactsJson;

    @Value("classpath:gamedata/genders.json")
    private Resource gendersJson;

    @Value("classpath:gamedata/guild_alignments.json")
    private Resource guildAlignmentsJson;

    @Value("classpath:gamedata/movement_modes.json")
    private Resource movementModesJson;

    @Value("classpath:gamedata/personalities.json")
    private Resource personalitiesJson;

    @Value("classpath:gamedata/power_types.json")
    private Resource powerTypesJson;

    /**
     * Returns all persisted alignments.
     *
     * @return the alignments currently stored in the repository
     */
    public List<AlignmentResponse> getAlignments() {
        return alignmentRepository.findAll()
                .stream()
                .map(AlignmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted allies.
     *
     * @return the allies currently stored in the repository
     */
    public List<AllyResponse> getAllies() {
        return allyRepository.findAll()
                .stream()
                .map(AllyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted artifacts.
     *
     * @return the artifacts currently stored in the repository
     */
    public List<ArtifactResponse> getArtifacts() {
        return artifactRepository.findAll()
                .stream()
                .map(ArtifactResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted genders.
     *
     * @return the genders currently stored in the repository
     */
    public List<GenderResponse> getGenders() {
        return genderRepository.findAll()
                .stream()
                .map(GenderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted guild alignments.
     *
     * @return the guild alignments currently stored in the repository
     */
    public List<GuildAlignmentResponse> getGuildAlignments() {
        return guildAlignmentRepository.findAll()
                .stream()
                .map(GuildAlignmentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted movement modes.
     *
     * @return the movement modes currently stored in the repository
     */
    public List<MovementModeResponse> getMovementModes() {
        return movementModeRepository.findAll()
                .stream()
                .map(MovementModeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted personalities.
     *
     * @return the personalities currently stored in the repository
     */
    public List<PersonalityResponse> getPersonalities() {
        return personalityRepository.findAll()
                .stream()
                .map(PersonalityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Returns all persisted power types.
     *
     * @return the power types currently stored in the repository
     */
    public List<PowerTypeResponse> getPowerTypes() {
        return powerTypeRepository.findAll()
                .stream()
                .map(PowerTypeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Loads alignments from {@code alignments.json} and persists any that are
     * not yet present (matched by census ID).
     */
    public void loadDefaultAlignments() {
        loadDefaults(alignmentsJson, new TypeReference<>() {
                }, Alignment::fromResource, a -> alignmentRepository.existsByCensusId(a.getCensusId()),
                alignmentRepository::save, "alignments");
    }

    /**
     * Loads allies from {@code allies.json} and persists any that are not yet
     * present (matched by census ID).
     */
    public void loadDefaultAllies() {
        loadDefaults(alliesJson, new TypeReference<>() {
                }, Ally::fromResource, a -> allyRepository.existsByCensusId(a.getCensusId()),
                allyRepository::save, "allies");
    }

    /**
     * Loads artifacts from {@code artifacts.json} and persists any that are
     * not yet present (matched by census ID).
     */
    public void loadDefaultArtifacts() {
        loadDefaults(artifactsJson, new TypeReference<>() {
                }, Artifact::fromResource, a -> artifactRepository.existsByCensusId(a.getCensusId()),
                artifactRepository::save, "artifacts");
    }

    /**
     * Loads genders from {@code genders.json} and persists any that are not
     * yet present (matched by census ID).
     */
    public void loadDefaultGenders() {
        loadDefaults(gendersJson, new TypeReference<>() {
                }, Gender::fromResource, g -> genderRepository.existsByCensusId(g.getCensusId()),
                genderRepository::save, "genders");
    }

    /**
     * Loads guild alignments from {@code guild_alignments.json} and persists
     * any that are not yet present (matched by census ID).
     */
    public void loadDefaultGuildAlignments() {
        loadDefaults(guildAlignmentsJson, new TypeReference<>() {
                }, GuildAlignment::fromResource, g -> guildAlignmentRepository.existsByCensusId(g.getCensusId()),
                guildAlignmentRepository::save, "guild alignments");
    }

    /**
     * Loads movement modes from {@code movement_modes.json} and persists any
     * that are not yet present (matched by census ID).
     */
    public void loadDefaultMovementModes() {
        loadDefaults(movementModesJson, new TypeReference<>() {
                }, MovementMode::fromResource, m -> movementModeRepository.existsByCensusId(m.getCensusId()),
                movementModeRepository::save, "movement modes");
    }

    /**
     * Loads personalities from {@code personalities.json} and persists any
     * that are not yet present (matched by census ID).
     */
    public void loadDefaultPersonalities() {
        loadDefaults(personalitiesJson, new TypeReference<>() {
                }, Personality::fromResource, p -> personalityRepository.existsByCensusId(p.getCensusId()),
                personalityRepository::save, "personalities");
    }

    /**
     * Loads power types from {@code power_types.json} and persists any that
     * are not yet present (matched by census ID).
     */
    public void loadDefaultPowerTypes() {
        loadDefaults(powerTypesJson, new TypeReference<>() {
                }, PowerType::fromResource, p -> powerTypeRepository.existsByCensusId(p.getCensusId()),
                powerTypeRepository::save, "power types");
    }

    /**
     * Shared implementation behind the {@code loadDefault*} methods: reads a
     * JSON classpath resource into a list of {@code R} resource DTOs, maps each
     * to its entity representation, and saves those not already present.
     *
     * @param json          classpath resource containing the JSON array to load
     * @param typeReference Jackson type for deserializing the JSON into {@code List<R>}
     * @param fromResource  converts a deserialized resource DTO into its entity
     * @param alreadyExists tests whether an entity with the same census ID is already stored
     * @param save          persists a new entity
     * @param label         short description of the data set, used in the error log message
     * @param <R>           resource DTO type deserialized from JSON
     * @param <E>           entity type persisted in the repository
     */
    private <R, E> void loadDefaults(Resource json, TypeReference<List<R>> typeReference,
                                     Function<R, E> fromResource, Predicate<E> alreadyExists,
                                     Consumer<E> save, String label) {
        try (InputStream in = json.getInputStream()) {
            List<R> resources = objectMapper.readValue(in, typeReference);

            resources
                    .stream()
                    .map(fromResource)
                    .filter(Predicate.not(alreadyExists))
                    .forEach(save);
        } catch (IOException ex) {
            LOGGER.error("Error loading {}", label, ex);
        }
    }
}
