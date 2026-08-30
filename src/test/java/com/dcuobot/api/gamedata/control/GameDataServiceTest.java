package com.dcuobot.api.gamedata.control;

import com.dcuobot.api.gamedata.dto.*;
import com.dcuobot.api.gamedata.entity.*;
import com.dcuobot.api.gamedata.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameDataServiceTest {

    private static final String TWO_ENTRIES_JSON = """
            [
              { "id": "1", "name": "First" },
              { "id": "2", "name": "Second" }
            ]
            """;

    @Mock
    private AlignmentRepository alignmentRepository;
    @Mock
    private AllyRepository allyRepository;
    @Mock
    private ArtifactRepository artifactRepository;
    @Mock
    private GenderRepository genderRepository;
    @Mock
    private GuildAlignmentRepository guildAlignmentRepository;
    @Mock
    private MovementModeRepository movementModeRepository;
    @Mock
    private PersonalityRepository personalityRepository;
    @Mock
    private PowerTypeRepository powerTypeRepository;

    private GameDataService gameDataService;

    @BeforeEach
    void setUp() {
        gameDataService = new GameDataService(
                alignmentRepository, allyRepository, artifactRepository, genderRepository,
                guildAlignmentRepository, movementModeRepository, personalityRepository, powerTypeRepository,
                new ObjectMapper());
    }

    @Test
    void getAlignments_mapsAllPersistedEntitiesToResponses() {
        Alignment alignment = new Alignment();
        alignment.setCensusId("1");
        alignment.setName("Good");
        when(alignmentRepository.findAll()).thenReturn(List.of(alignment));

        List<AlignmentResponse> responses = gameDataService.getAlignments();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Good");
    }

    @Test
    void getAllies_mapsAllPersistedEntitiesToResponses() {
        Ally ally = new Ally();
        ally.setCensusId("1");
        ally.setName("Batman");
        when(allyRepository.findAll()).thenReturn(List.of(ally));

        List<AllyResponse> responses = gameDataService.getAllies();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Batman");
    }

    @Test
    void getArtifacts_mapsAllPersistedEntitiesToResponses() {
        Artifact artifact = new Artifact();
        artifact.setCensusId("1");
        artifact.setName("Heart of Darkness");
        artifact.setImageUrl("https://example.com/artifact.png");
        artifact.setDiscordEmojiId("123456789");
        when(artifactRepository.findAll()).thenReturn(List.of(artifact));

        List<ArtifactResponse> responses = gameDataService.getArtifacts();

        assertThat(responses).hasSize(1);
        ArtifactResponse response = responses.getFirst();
        assertThat(response.getCensusId()).isEqualTo("1");
        assertThat(response.getName()).isEqualTo("Heart of Darkness");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/artifact.png");
        assertThat(response.getDiscordEmojiId()).isEqualTo("123456789");
    }

    @Test
    void getGenders_mapsAllPersistedEntitiesToResponses() {
        Gender gender = new Gender();
        gender.setCensusId("1");
        gender.setName("Female");
        gender.setImageUrl("https://example.com/gender.png");
        when(genderRepository.findAll()).thenReturn(List.of(gender));

        List<GenderResponse> responses = gameDataService.getGenders();

        assertThat(responses).hasSize(1);
        GenderResponse response = responses.getFirst();
        assertThat(response.getCensusId()).isEqualTo("1");
        assertThat(response.getName()).isEqualTo("Female");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/gender.png");
    }

    @Test
    void getGuildAlignments_mapsAllPersistedEntitiesToResponses() {
        GuildAlignment guildAlignment = new GuildAlignment();
        guildAlignment.setCensusId("1");
        guildAlignment.setName("Hero");
        when(guildAlignmentRepository.findAll()).thenReturn(List.of(guildAlignment));

        List<GuildAlignmentResponse> responses = gameDataService.getGuildAlignments();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Hero");
    }

    @Test
    void getMovementModes_mapsAllPersistedEntitiesToResponses() {
        MovementMode movementMode = new MovementMode();
        movementMode.setCensusId("1");
        movementMode.setName("Flight");
        when(movementModeRepository.findAll()).thenReturn(List.of(movementMode));

        List<MovementModeResponse> responses = gameDataService.getMovementModes();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Flight");
    }

    @Test
    void getPersonalities_mapsAllPersistedEntitiesToResponses() {
        Personality personality = new Personality();
        personality.setCensusId("1");
        personality.setName("Fierce");
        when(personalityRepository.findAll()).thenReturn(List.of(personality));

        List<PersonalityResponse> responses = gameDataService.getPersonalities();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Fierce");
    }

    @Test
    void getPowerTypes_mapsAllPersistedEntitiesToResponses() {
        PowerType powerType = new PowerType();
        powerType.setCensusId("1");
        powerType.setName("Fire");
        when(powerTypeRepository.findAll()).thenReturn(List.of(powerType));

        List<PowerTypeResponse> responses = gameDataService.getPowerTypes();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCensusId()).isEqualTo("1");
        assertThat(responses.getFirst().getName()).isEqualTo("Fire");
    }

    @Test
    void getAlignments_returnsEmptyListWhenNoneArePersisted() {
        when(alignmentRepository.findAll()).thenReturn(List.of());

        assertThat(gameDataService.getAlignments()).isEmpty();
    }

    @Test
    void loadDefaultAlignments_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "alignmentsJson",
                gameDataService::loadDefaultAlignments,
                (censusId, exists) -> when(alignmentRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(alignmentRepository).save(argThat((Alignment a) -> "1".equals(a.getCensusId())));
                    verify(alignmentRepository, never()).save(argThat((Alignment a) -> "2".equals(a.getCensusId())));
                });
    }

    @Test
    void loadDefaultAllies_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "alliesJson",
                gameDataService::loadDefaultAllies,
                (censusId, exists) -> when(allyRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(allyRepository).save(argThat((Ally a) -> "1".equals(a.getCensusId())));
                    verify(allyRepository, never()).save(argThat((Ally a) -> "2".equals(a.getCensusId())));
                });
    }

    @Test
    void loadDefaultArtifacts_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "artifactsJson",
                gameDataService::loadDefaultArtifacts,
                (censusId, exists) -> when(artifactRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(artifactRepository).save(argThat((Artifact a) -> "1".equals(a.getCensusId())));
                    verify(artifactRepository, never()).save(argThat((Artifact a) -> "2".equals(a.getCensusId())));
                });
    }

    @Test
    void loadDefaultGenders_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "gendersJson",
                gameDataService::loadDefaultGenders,
                (censusId, exists) -> when(genderRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(genderRepository).save(argThat((Gender g) -> "1".equals(g.getCensusId())));
                    verify(genderRepository, never()).save(argThat((Gender g) -> "2".equals(g.getCensusId())));
                });
    }

    @Test
    void loadDefaultGuildAlignments_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "guildAlignmentsJson",
                gameDataService::loadDefaultGuildAlignments,
                (censusId, exists) -> when(guildAlignmentRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(guildAlignmentRepository).save(argThat((GuildAlignment g) -> "1".equals(g.getCensusId())));
                    verify(guildAlignmentRepository, never()).save(argThat((GuildAlignment g) -> "2".equals(g.getCensusId())));
                });
    }

    @Test
    void loadDefaultMovementModes_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "movementModesJson",
                gameDataService::loadDefaultMovementModes,
                (censusId, exists) -> when(movementModeRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(movementModeRepository).save(argThat((MovementMode m) -> "1".equals(m.getCensusId())));
                    verify(movementModeRepository, never()).save(argThat((MovementMode m) -> "2".equals(m.getCensusId())));
                });
    }

    @Test
    void loadDefaultPersonalities_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "personalitiesJson",
                gameDataService::loadDefaultPersonalities,
                (censusId, exists) -> when(personalityRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(personalityRepository).save(argThat((Personality p) -> "1".equals(p.getCensusId())));
                    verify(personalityRepository, never()).save(argThat((Personality p) -> "2".equals(p.getCensusId())));
                });
    }

    @Test
    void loadDefaultPowerTypes_savesOnlyEntriesNotAlreadyPersisted() {
        assertOnlyMissingEntrySaved(
                "powerTypesJson",
                gameDataService::loadDefaultPowerTypes,
                (censusId, exists) -> when(powerTypeRepository.existsByCensusId(censusId)).thenReturn(exists),
                () -> {
                    verify(powerTypeRepository).save(argThat((PowerType p) -> "1".equals(p.getCensusId())));
                    verify(powerTypeRepository, never()).save(argThat((PowerType p) -> "2".equals(p.getCensusId())));
                });
    }

    @Test
    void loadDefault_swallowsResourceReadFailuresInsteadOfThrowing() throws IOException {
        List<ExceptionScenario> scenarios = List.of(
                new ExceptionScenario("alignmentsJson", gameDataService::loadDefaultAlignments, alignmentRepository),
                new ExceptionScenario("alliesJson", gameDataService::loadDefaultAllies, allyRepository),
                new ExceptionScenario("artifactsJson", gameDataService::loadDefaultArtifacts, artifactRepository),
                new ExceptionScenario("gendersJson", gameDataService::loadDefaultGenders, genderRepository),
                new ExceptionScenario("guildAlignmentsJson", gameDataService::loadDefaultGuildAlignments, guildAlignmentRepository),
                new ExceptionScenario("movementModesJson", gameDataService::loadDefaultMovementModes, movementModeRepository),
                new ExceptionScenario("personalitiesJson", gameDataService::loadDefaultPersonalities, personalityRepository),
                new ExceptionScenario("powerTypesJson", gameDataService::loadDefaultPowerTypes, powerTypeRepository));

        for (ExceptionScenario scenario : scenarios) {
            Resource unreadableResource = mock(Resource.class);
            when(unreadableResource.getInputStream()).thenThrow(new IOException("boom"));
            ReflectionTestUtils.setField(gameDataService, scenario.fieldName(), unreadableResource);

            assertThatCode(scenario.invoke()::run).doesNotThrowAnyException();
            verifyNoInteractions(scenario.repository());
        }
    }

    private void assertOnlyMissingEntrySaved(String fieldName, Runnable invoke,
                                             BiConsumer<String, Boolean> stubExists, Runnable verifySaved) {
        stubExists.accept("1", false);
        stubExists.accept("2", true);
        setJsonResource(fieldName, TWO_ENTRIES_JSON);

        invoke.run();

        verifySaved.run();
    }

    private void setJsonResource(String fieldName, String json) {
        ReflectionTestUtils.setField(gameDataService, fieldName,
                new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8)));
    }

    private record ExceptionScenario(String fieldName, Runnable invoke, Object repository) {
    }
}
