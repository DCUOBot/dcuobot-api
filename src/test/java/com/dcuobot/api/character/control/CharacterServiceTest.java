package com.dcuobot.api.character.control;

import com.dcuobot.api.census.dto.character.*;
import com.dcuobot.api.census.client.CensusClient;
import com.dcuobot.api.census.exception.CensusException;
import com.dcuobot.api.census.exception.MissingDataException;
import com.dcuobot.api.character.dto.CharacterAllyResponse;
import com.dcuobot.api.character.dto.CharacterArtifactResponse;
import com.dcuobot.api.character.dto.CharacterResponse;
import com.dcuobot.api.character.exception.CharacterNotFoundException;
import com.dcuobot.api.common.worldid.InvalidWorldIdException;
import com.dcuobot.api.common.worldid.WorldIdHelpers;
import com.dcuobot.api.gamedata.entity.*;
import com.dcuobot.api.gamedata.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    @Mock
    private CensusClient censusClient;
    @Mock
    private WorldIdHelpers worldIdHelpers;
    @Mock
    private ArtifactRepository artifactRepository;
    @Mock
    private AllyRepository allyRepository;
    @Mock
    private AlignmentRepository alignmentRepository;
    @Mock
    private PowerTypeRepository powerTypeRepository;
    @Mock
    private MovementModeRepository movementModeRepository;
    @Mock
    private PersonalityRepository personalityRepository;
    @Mock
    private GenderRepository genderRepository;

    private CharacterService characterService;

    @BeforeEach
    void setUp() {
        characterService = new CharacterService(
                censusClient, worldIdHelpers, artifactRepository, allyRepository, alignmentRepository,
                powerTypeRepository, movementModeRepository, personalityRepository, genderRepository);
        lenient().when(worldIdHelpers.isValidWorldId("1000")).thenReturn(true);
    }

    @Test
    void getCharacter_throwsInvalidWorldIdException_whenWorldIdIsInvalid() {
        when(worldIdHelpers.isValidWorldId("9999")).thenReturn(false);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "9999"))
                .isInstanceOf(InvalidWorldIdException.class);
        verifyNoInteractions(censusClient);
    }

    @Test
    void getCharacter_returnsFullyPopulatedResponse_whenCharacterHasGuildArtifactsAndAllies() {
        CensusCharacter character = defaultCharacter();
        character.setGuildRoster(List.of(guildRoster("55")));
        stubCharacterSearch(character);
        stubReferenceData(character);

        CensusCharacterGuild guild = new CensusCharacterGuild();
        guild.setGuildId("55");
        guild.setName("Justice League");
        CensusCharacterGuildList guildList = new CensusCharacterGuildList();
        guildList.setGuildList(List.of(guild));
        when(censusClient.getCharacterGuild("55")).thenReturn(guildList);

        CensusCharacterItem art1 = item("art1", "23");
        CensusCharacterItem art2 = item("art2", "24");
        CensusCharacterItem art3 = item("art3", "31");
        List<CensusCharacterItem> artifactItems = new ArrayList<>(List.of(artifactItemsWithThirdAt(8, art1, art2, art3)));
        artifactItems.add(item("art4", "34"));
        artifactItems.add(item("art5", "35"));
        when(censusClient.getCharacterArtifacts("100"))
                .thenReturn(itemList(artifactItems.toArray(new CensusCharacterItem[0])));
        stubArtifact("art1", "Heart of Darkness");
        stubArtifact("art2", "Soul of Scarabus");
        stubArtifact("art3", "Wonder");
        stubArtifact("art4", "Eye of Gemini");
        stubArtifact("art5", "Necklace of Cagliostro");

        CensusCharacterItem ally1 = item("ally1", "31");
        CensusCharacterItem ally2 = item("ally2", "32");
        when(censusClient.getCharacterAllies("100")).thenReturn(itemList(ally1, ally2));
        stubAlly("ally1", "Robin");
        stubAlly("ally2", "Batgirl");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getCharacterId()).isEqualTo("100");
        assertThat(response.getWorldId()).isEqualTo("1000");
        assertThat(response.getName()).isEqualTo("Batman");
        assertThat(response.getAlignment()).isEqualTo("Good");
        assertThat(response.getGender()).isEqualTo("Male");
        assertThat(response.getPowerType()).isEqualTo("Fire");
        assertThat(response.getMovementMode()).isEqualTo("Flight");
        assertThat(response.getPersonality()).isEqualTo("Fierce");
        assertThat(response.getCombatRating()).isEqualTo(500);
        assertThat(response.getPvpCombatRating()).isEqualTo(400);
        assertThat(response.getSkillPoints()).isEqualTo(200);

        assertThat(response.getStats().getHealth()).isEqualTo(10000);
        assertThat(response.getStats().getPower()).isEqualTo(5000);
        assertThat(response.getStats().getDefense()).isEqualTo(1000);
        assertThat(response.getStats().getToughness()).isEqualTo(900);
        assertThat(response.getStats().getMight()).isEqualTo(800);
        assertThat(response.getStats().getPrecision()).isEqualTo(700);
        assertThat(response.getStats().getRestoration()).isEqualTo(600);
        assertThat(response.getStats().getVitalization()).isEqualTo(500);
        assertThat(response.getStats().getDominance()).isEqualTo(400);

        assertThat(response.getImage().getUrl())
                .isEqualTo("https://dcuo.bot/api/v1/census/characters/100/image?genderId=0");
        assertThat(response.getImage().getAltUrl()).isEqualTo("https://example.com/gender.png");

        assertThat(response.getGuild().getId()).isEqualTo("55");
        assertThat(response.getGuild().getName()).isEqualTo("Justice League");

        assertThat(response.getArtifacts())
                .extracting(CharacterArtifactResponse::getId)
                .containsExactlyInAnyOrder("art1", "art2", "art3", "art4", "art5");

        assertThat(response.getAllies())
                .extracting(CharacterAllyResponse::getId)
                .containsExactlyInAnyOrder("ally1", "ally2");
        assertThat(response.getAllies())
                .filteredOn(a -> a.getId().equals("ally1"))
                .singleElement()
                .satisfies(a -> assertThat(a.isCombat()).isTrue());
        assertThat(response.getAllies())
                .filteredOn(a -> a.getId().equals("ally2"))
                .singleElement()
                .satisfies(a -> assertThat(a.isCombat()).isFalse());
    }

    @Test
    void getCharacter_omitsGuild_whenCharacterHasNoGuildRoster() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyArtifactsAndAllies("100");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getGuild()).isNull();
        verify(censusClient, never()).getCharacterGuild(anyString());
    }

    @Test
    void getCharacter_usesExactMatch_whenNameIsThreeOrMoreCharacters() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyArtifactsAndAllies("100");

        characterService.getCharacter("Batman", "1000");

        verify(censusClient).getCharacter("Batman", "1000", false);
    }

    @Test
    void getCharacter_usesWildcardSearch_whenNameIsShorterThanThreeCharacters() {
        CensusCharacter character = defaultCharacter();
        character.setName("Bo");
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(character));
        when(censusClient.getCharacter("Bo", "1000", true)).thenReturn(characterList);
        stubReferenceData(character);
        stubEmptyArtifactsAndAllies("100");

        characterService.getCharacter("Bo", "1000");

        verify(censusClient).getCharacter("Bo", "1000", true);
    }

    @Test
    void getCharacter_throwsCensusException_whenCharacterSearchResponseIsNull() {
        when(censusClient.getCharacter(anyString(), anyString(), anyBoolean())).thenReturn(null);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacter_throwsCensusException_whenCharacterSearchListIsNull() {
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(null);
        when(censusClient.getCharacter(anyString(), anyString(), anyBoolean())).thenReturn(characterList);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacter_throwsCharacterNotFoundException_whenNoCharacterMatches() {
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of());
        when(censusClient.getCharacter(anyString(), anyString(), anyBoolean())).thenReturn(characterList);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CharacterNotFoundException.class);
    }

    @Test
    void getCharacter_throwsCensusException_whenGuildResponseIsNull() {
        CensusCharacter character = defaultCharacter();
        character.setGuildRoster(List.of(guildRoster("55")));
        stubCharacterSearch(character);
        when(censusClient.getCharacterGuild("55")).thenReturn(null);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacter_throwsCensusException_whenArtifactResponseIsNull() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        when(censusClient.getCharacterArtifacts("100")).thenReturn(null);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacter_throwsCensusException_whenAllyResponseIsNull() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        when(censusClient.getCharacterArtifacts("100")).thenReturn(itemList());
        when(censusClient.getCharacterAllies("100")).thenReturn(null);

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacter_throwsMissingDataException_whenAlignmentIsUnknown() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        when(alignmentRepository.findByCensusId("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> characterService.getCharacter("Batman", "1000"))
                .isInstanceOf(MissingDataException.class);
    }

    @Test
    void getCharacter_selectsThirdArtifactFromIndexEight_whenPresent() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyAllies("100");

        CensusCharacterItem art1 = item("art1", "23");
        CensusCharacterItem art2 = item("art2", "24");
        CensusCharacterItem art3 = item("art3", "31");
        CensusCharacterItem[] items = artifactItemsWithThirdAt(8, art1, art2, art3);
        when(censusClient.getCharacterArtifacts("100")).thenReturn(itemList(items));
        stubArtifact("art1", "Heart of Darkness");
        stubArtifact("art2", "Soul of Scarabus");
        stubArtifact("art3", "Wonder");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getArtifacts())
                .extracting(CharacterArtifactResponse::getId)
                .containsExactlyInAnyOrder("art1", "art2", "art3");
        verify(artifactRepository, never()).findByCensusId(items[7].getItemId());
    }

    @Test
    void getCharacter_fallsBackToIndexSeven_forThirdArtifact_whenIndexEightAbsent() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);

        CensusCharacterItem art1 = item("art1", "23");
        CensusCharacterItem art2 = item("art2", "24");
        CensusCharacterItem art3 = item("art3", "30");
        CensusCharacterItem[] items = artifactItemsWithThirdAt(7, art1, art2, art3);
        when(censusClient.getCharacterArtifacts("100")).thenReturn(itemList(items));
        stubArtifact("art1", "Heart of Darkness");
        stubArtifact("art2", "Soul of Scarabus");
        stubArtifact("art3", "Wonder");
        stubEmptyAllies("100");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getArtifacts())
                .extracting(CharacterArtifactResponse::getId)
                .containsExactlyInAnyOrder("art1", "art2", "art3");
    }

    @Test
    void getCharacter_skipsArtifact_whenNotFoundInRepository() {
        CensusCharacter character = defaultCharacter();
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyAllies("100");

        when(censusClient.getCharacterArtifacts("100")).thenReturn(itemList(item("unknown", "23")));
        when(artifactRepository.findByCensusId("unknown")).thenReturn(Optional.empty());

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getArtifacts()).isEmpty();
    }

    @Test
    void getCharacter_defaultsStatsToZero_whenCensusFieldsAreNull() {
        CensusCharacter character = defaultCharacter();
        character.setMaxHealth(null);
        character.setMaxPower(null);
        character.setDefense(null);
        character.setToughness(null);
        character.setMight(null);
        character.setPrecision(null);
        character.setRestoration(null);
        character.setVitalization(null);
        character.setDominance(null);
        character.setCombatRating(null);
        character.setPvpCombatRating(null);
        character.setSkillPoints(null);
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyArtifactsAndAllies("100");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getStats().getHealth()).isZero();
        assertThat(response.getStats().getPower()).isZero();
        assertThat(response.getStats().getDefense()).isZero();
        assertThat(response.getStats().getToughness()).isZero();
        assertThat(response.getStats().getMight()).isZero();
        assertThat(response.getStats().getPrecision()).isZero();
        assertThat(response.getStats().getRestoration()).isZero();
        assertThat(response.getStats().getVitalization()).isZero();
        assertThat(response.getStats().getDominance()).isZero();
        assertThat(response.getCombatRating()).isZero();
        assertThat(response.getPvpCombatRating()).isZero();
        assertThat(response.getSkillPoints()).isZero();
    }

    @Test
    void getCharacter_setsGenderFemale_whenGenderIdIsNotZero() {
        CensusCharacter character = defaultCharacter();
        character.setGenderId("1");
        stubCharacterSearch(character);
        stubReferenceData(character);
        stubEmptyArtifactsAndAllies("100");

        CharacterResponse response = characterService.getCharacter("Batman", "1000");

        assertThat(response.getGender()).isEqualTo("Female");
    }

    @Test
    void getCharacterImage_returnsPaperdollImage_whenPaperdollExists() throws IOException {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            BufferedImage paperdoll = mock(BufferedImage.class);
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(paperdoll);
            stubImageWrite(imageIO, paperdoll, "paperdoll-bytes");

            byte[] result = characterService.getCharacterImage("100", "0");

            assertThat(result).isEqualTo("paperdoll-bytes".getBytes(StandardCharsets.UTF_8));
            verifyNoInteractions(censusClient, genderRepository);
        }
    }

    @Test
    void getCharacterImage_fallsBackToGenderImage_whenPaperdollNotFound() throws IOException {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(null);

            BufferedImage fallback = mock(BufferedImage.class);
            imageIO.when(() -> ImageIO.read(urlEndingWith("/gender.png"))).thenReturn(fallback);
            stubImageWrite(imageIO, fallback, "fallback-bytes");

            Gender gender = new Gender();
            gender.setImageUrl("https://example.com/gender.png");
            when(genderRepository.findByCensusId("0")).thenReturn(Optional.of(gender));

            byte[] result = characterService.getCharacterImage("100", "0");

            assertThat(result).isEqualTo("fallback-bytes".getBytes(StandardCharsets.UTF_8));
            verifyNoInteractions(censusClient);
        }
    }

    @Test
    void getCharacterImage_fallsBackToGenderImage_whenPaperdollReadThrowsIOException() throws IOException {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenThrow(new IOException("unreachable"));

            BufferedImage fallback = mock(BufferedImage.class);
            imageIO.when(() -> ImageIO.read(urlEndingWith("/gender.png"))).thenReturn(fallback);
            stubImageWrite(imageIO, fallback, "fallback-bytes");

            Gender gender = new Gender();
            gender.setImageUrl("https://example.com/gender.png");
            when(genderRepository.findByCensusId("0")).thenReturn(Optional.of(gender));

            byte[] result = characterService.getCharacterImage("100", "0");

            assertThat(result).isEqualTo("fallback-bytes".getBytes(StandardCharsets.UTF_8));
        }
    }

    @Test
    void getCharacterImage_looksUpGenderFromCensus_whenGenderIdIsBlank() throws IOException {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(null);

            CensusCharacterGender censusGender = new CensusCharacterGender();
            censusGender.setGenderId("1");
            CensusCharacterGenderList genderList = new CensusCharacterGenderList();
            genderList.setCharacterList(List.of(censusGender));
            when(censusClient.getCharacterGender("100")).thenReturn(genderList);

            Gender gender = new Gender();
            gender.setImageUrl("https://example.com/female.png");
            when(genderRepository.findByCensusId("1")).thenReturn(Optional.of(gender));

            BufferedImage fallback = mock(BufferedImage.class);
            imageIO.when(() -> ImageIO.read(urlEndingWith("/female.png"))).thenReturn(fallback);
            stubImageWrite(imageIO, fallback, "fallback-bytes");

            byte[] result = characterService.getCharacterImage("100", " ");

            assertThat(result).isEqualTo("fallback-bytes".getBytes(StandardCharsets.UTF_8));
            verify(censusClient).getCharacterGender("100");
        }
    }

    @Test
    void getCharacterImage_throwsCensusException_whenGenderLookupResponseIsNull() {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(null);
            when(censusClient.getCharacterGender("100")).thenReturn(null);

            assertThatThrownBy(() -> characterService.getCharacterImage("100", null))
                    .isInstanceOf(CensusException.class);
        }
    }

    @Test
    void getCharacterImage_throwsCharacterNotFoundException_whenGenderLookupListIsEmpty() {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(null);
            CensusCharacterGenderList genderList = new CensusCharacterGenderList();
            genderList.setCharacterList(List.of());
            when(censusClient.getCharacterGender("100")).thenReturn(genderList);

            assertThatThrownBy(() -> characterService.getCharacterImage("100", null))
                    .isInstanceOf(CharacterNotFoundException.class);
        }
    }

    @Test
    void getCharacterImage_throwsMissingDataException_whenGenderNotInRepository() {
        try (MockedStatic<ImageIO> imageIO = mockStatic(ImageIO.class)) {
            imageIO.when(() -> ImageIO.read(urlEndingWith("/paperdoll/100"))).thenReturn(null);
            when(genderRepository.findByCensusId("0")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> characterService.getCharacterImage("100", "0"))
                    .isInstanceOf(MissingDataException.class);
        }
    }

    @Test
    void getCharacterRanking_throwsCensusException_whenResponseIsNull() {
        when(censusClient.getCharacterRanking("1000", "combat_rating")).thenReturn(null);

        assertThatThrownBy(() -> characterService.getCharacterRanking("1000", "combat_rating"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacterRanking_throwsCensusException_whenCharacterListIsNull() {
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(null);
        when(censusClient.getCharacterRanking("1000", "combat_rating")).thenReturn(characterList);

        assertThatThrownBy(() -> characterService.getCharacterRanking("1000", "combat_rating"))
                .isInstanceOf(CensusException.class);
    }

    @Test
    void getCharacterRanking_returnsResolvedCharacterResponse_whenReferenceDataIsFound() {
        CensusCharacter character = defaultCharacter();
        stubRankingSearch(character, "combat_rating");
        stubRankingReferenceData(character);

        Collection<CharacterResponse> responses = characterService.getCharacterRanking("1000", "combat_rating");

        assertThat(responses).hasSize(1);
        CharacterResponse response = responses.iterator().next();
        assertThat(response.getCharacterId()).isEqualTo("100");
        assertThat(response.getWorldId()).isEqualTo("1000");
        assertThat(response.getName()).isEqualTo("Batman");
        assertThat(response.getAlignment()).isEqualTo("Good");
        assertThat(response.getGender()).isEqualTo("Male");
        assertThat(response.getPowerType()).isEqualTo("Fire");
        assertThat(response.getMovementMode()).isEqualTo("Flight");
        assertThat(response.getPersonality()).isEqualTo("Fierce");
        assertThat(response.getCombatRating()).isEqualTo(500);
        assertThat(response.getPvpCombatRating()).isEqualTo(400);
        assertThat(response.getSkillPoints()).isEqualTo(200);
        assertThat(response.getStats().getHealth()).isEqualTo(10000);
        assertThat(response.getImage().getUrl())
                .isEqualTo("https://dcuo.bot/api/v1/census/characters/100/image?genderId=0");
        assertThat(response.getImage().getAltUrl()).isEqualTo("https://example.com/gender.png");
        assertThat(response.getGuild()).isNull();
        assertThat(response.getArtifacts()).isNull();
        assertThat(response.getAllies()).isNull();

        verifyNoInteractions(artifactRepository, allyRepository);
        verify(censusClient, never()).getCharacterGuild(anyString());
    }

    @Test
    void getCharacterRanking_mapsEachCharacterInTheList() {
        CensusCharacter batman = defaultCharacter();
        CensusCharacter robin = defaultCharacter();
        robin.setCharacterId("200");
        robin.setName("Robin");

        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(batman, robin));
        when(censusClient.getCharacterRanking("1000", "combat_rating")).thenReturn(characterList);
        stubRankingReferenceData(batman);

        Collection<CharacterResponse> responses = characterService.getCharacterRanking("1000", "combat_rating");

        assertThat(responses)
                .extracting(CharacterResponse::getCharacterId, CharacterResponse::getName)
                .containsExactly(tuple("100", "Batman"), tuple("200", "Robin"));
    }

    @Test
    void getCharacterRanking_leavesReferenceFieldsNull_whenNotFoundInRepository() {
        CensusCharacter character = defaultCharacter();
        stubRankingSearch(character, "combat_rating");
        when(alignmentRepository.findAll()).thenReturn(List.of());
        when(powerTypeRepository.findAll()).thenReturn(List.of());
        when(movementModeRepository.findAll()).thenReturn(List.of());
        when(personalityRepository.findAll()).thenReturn(List.of());
        stubRankingGenderImage(character);

        CharacterResponse response = characterService.getCharacterRanking("1000", "combat_rating")
                .iterator().next();

        assertThat(response.getAlignment()).isNull();
        assertThat(response.getPowerType()).isNull();
        assertThat(response.getMovementMode()).isNull();
        assertThat(response.getPersonality()).isNull();
    }

    @Test
    void getCharacterRanking_defaultsRatingsAndStatsToZero_whenCensusFieldsAreNull() {
        CensusCharacter character = defaultCharacter();
        character.setCombatRating(null);
        character.setPvpCombatRating(null);
        character.setSkillPoints(null);
        character.setMaxHealth(null);
        stubRankingSearch(character, "combat_rating");
        stubRankingReferenceData(character);

        CharacterResponse response = characterService.getCharacterRanking("1000", "combat_rating")
                .iterator().next();

        assertThat(response.getCombatRating()).isZero();
        assertThat(response.getPvpCombatRating()).isZero();
        assertThat(response.getSkillPoints()).isZero();
        assertThat(response.getStats().getHealth()).isZero();
    }

    @Test
    void getCharacterRanking_setsGenderFemale_whenGenderIdIsNotZero() {
        CensusCharacter character = defaultCharacter();
        character.setGenderId("1");
        stubRankingSearch(character, "combat_rating");
        stubRankingReferenceData(character);

        CharacterResponse response = characterService.getCharacterRanking("1000", "combat_rating")
                .iterator().next();

        assertThat(response.getGender()).isEqualTo("Female");
    }

    @Test
    void getCharacterRanking_throwsMissingDataException_whenGenderReferenceDataIsMissing() {
        CensusCharacter character = defaultCharacter();
        stubRankingSearch(character, "combat_rating");
        when(alignmentRepository.findAll()).thenReturn(List.of());
        when(powerTypeRepository.findAll()).thenReturn(List.of());
        when(movementModeRepository.findAll()).thenReturn(List.of());
        when(personalityRepository.findAll()).thenReturn(List.of());
        when(genderRepository.findByCensusId(character.getGenderId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> characterService.getCharacterRanking("1000", "combat_rating"))
                .isInstanceOf(MissingDataException.class);
    }

    private void stubRankingSearch(CensusCharacter character, String sort) {
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(character));
        when(censusClient.getCharacterRanking(character.getWorldId(), sort)).thenReturn(characterList);
    }

    private void stubRankingReferenceData(CensusCharacter character) {
        Alignment alignment = new Alignment();
        alignment.setCensusId(character.getAlignmentId());
        alignment.setName("Good");
        when(alignmentRepository.findAll()).thenReturn(List.of(alignment));

        PowerType powerType = new PowerType();
        powerType.setCensusId(character.getPowerTypeId());
        powerType.setName("Fire");
        when(powerTypeRepository.findAll()).thenReturn(List.of(powerType));

        MovementMode movementMode = new MovementMode();
        movementMode.setCensusId(character.getMovementModeId());
        movementMode.setName("Flight");
        when(movementModeRepository.findAll()).thenReturn(List.of(movementMode));

        Personality personality = new Personality();
        personality.setCensusId(character.getPersonalityId());
        personality.setName("Fierce");
        when(personalityRepository.findAll()).thenReturn(List.of(personality));

        stubRankingGenderImage(character);
    }

    private void stubRankingGenderImage(CensusCharacter character) {
        Gender gender = new Gender();
        gender.setImageUrl("https://example.com/gender.png");
        when(genderRepository.findByCensusId(character.getGenderId())).thenReturn(Optional.of(gender));
    }

    private static URL urlEndingWith(String suffix) {
        return argThat(url -> url != null && url.toString().endsWith(suffix));
    }

    private void stubImageWrite(MockedStatic<ImageIO> imageIO, BufferedImage image, String content) {
        imageIO.when(() -> ImageIO.write(eq(image), eq("png"), any(OutputStream.class)))
                .thenAnswer(invocation -> {
                    OutputStream out = invocation.getArgument(2);
                    out.write(content.getBytes(StandardCharsets.UTF_8));
                    return true;
                });
    }

    private CensusCharacter defaultCharacter() {
        CensusCharacter character = new CensusCharacter();
        character.setCharacterId("100");
        character.setWorldId("1000");
        character.setName("Batman");
        character.setAlignmentId("1");
        character.setGenderId("0");
        character.setPowerTypeId("2");
        character.setMovementModeId("3");
        character.setPersonalityId("4");
        character.setCombatRating("500");
        character.setPvpCombatRating("400");
        character.setSkillPoints("200");
        character.setMaxHealth("10000");
        character.setMaxPower("5000");
        character.setDefense("1000");
        character.setToughness("900");
        character.setMight("800");
        character.setPrecision("700");
        character.setRestoration("600");
        character.setVitalization("500");
        character.setDominance("400");
        return character;
    }

    private CensusGuildRoster guildRoster(String guildId) {
        CensusGuildRoster roster = new CensusGuildRoster();
        roster.setGuildId(guildId);
        return roster;
    }

    private CensusCharacterItem item(String itemId, String slotId) {
        CensusCharacterItem item = new CensusCharacterItem();
        item.setItemId(itemId);
        item.setEquipmentSlotId(slotId);
        return item;
    }

    private CensusCharacterItem[] artifactItemsWithThirdAt(
            int thirdIndex, CensusCharacterItem art1, CensusCharacterItem art2, CensusCharacterItem third) {
        List<CensusCharacterItem> items = new ArrayList<>(List.of(art1, art2));
        for (int slot = 25; items.size() < thirdIndex; slot++) {
            items.add(item("filler" + slot, String.valueOf(slot)));
        }
        items.add(third);
        return items.toArray(new CensusCharacterItem[0]);
    }

    private CensusCharacterItemList itemList(CensusCharacterItem... items) {
        CensusCharacterItemList list = new CensusCharacterItemList();
        list.setCharactersItemList(items);
        return list;
    }

    private void stubCharacterSearch(CensusCharacter character) {
        CensusCharacterList characterList = new CensusCharacterList();
        characterList.setCharacterList(List.of(character));
        boolean wildcard = character.getName().length() < 3;
        when(censusClient.getCharacter(character.getName(), character.getWorldId(), wildcard))
                .thenReturn(characterList);
    }

    private void stubReferenceData(CensusCharacter character) {
        Alignment alignment = new Alignment();
        alignment.setName("Good");
        when(alignmentRepository.findByCensusId(character.getAlignmentId())).thenReturn(Optional.of(alignment));

        PowerType powerType = new PowerType();
        powerType.setName("Fire");
        when(powerTypeRepository.findByCensusId(character.getPowerTypeId())).thenReturn(Optional.of(powerType));

        MovementMode movementMode = new MovementMode();
        movementMode.setName("Flight");
        when(movementModeRepository.findByCensusId(character.getMovementModeId())).thenReturn(Optional.of(movementMode));

        Personality personality = new Personality();
        personality.setName("Fierce");
        when(personalityRepository.findByCensusId(character.getPersonalityId())).thenReturn(Optional.of(personality));

        Gender gender = new Gender();
        gender.setImageUrl("https://example.com/gender.png");
        when(genderRepository.findByCensusId(character.getGenderId())).thenReturn(Optional.of(gender));
    }

    private void stubArtifact(String censusId, String name) {
        Artifact artifact = new Artifact();
        artifact.setCensusId(censusId);
        artifact.setName(name);
        when(artifactRepository.findByCensusId(censusId)).thenReturn(Optional.of(artifact));
    }

    private void stubAlly(String censusId, String name) {
        Ally ally = new Ally();
        ally.setCensusId(censusId);
        ally.setName(name);
        when(allyRepository.findByCensusId(censusId)).thenReturn(Optional.of(ally));
    }

    private void stubEmptyArtifactsAndAllies(String characterId) {
        when(censusClient.getCharacterArtifacts(characterId)).thenReturn(itemList());
        stubEmptyAllies(characterId);
    }

    private void stubEmptyAllies(String characterId) {
        when(censusClient.getCharacterAllies(characterId)).thenReturn(itemList());
    }
}
