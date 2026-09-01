package com.dcuobot.api.census.client;

import com.dcuobot.api.census.dto.character.*;
import com.dcuobot.api.census.dto.guild.CensusGuildList;
import com.dcuobot.api.census.dto.guild.CensusGuildRosterList;
import com.dcuobot.api.census.dto.status.CensusGameServerStatusList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "censusClient", url = "${census.base-url}", fallback = CensusClientFallback.class)
public interface CensusClient {
    @GetMapping(value = "/get/dcuo/character", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusCharacterList getCharacter(@RequestParam("name") String name,
                                     @RequestParam("c%3Acase") String cCase,
                                     @RequestParam("world_id") String worldId,
                                     @RequestParam("c%3Ajoin") String cJoin,
                                     @RequestParam("c%3Ashow") String cShow,
                                     @RequestParam("c%3Alimit") String cLimit,
                                     @RequestParam("c%3AexactMatchFirst") String cExactMatchFirst,
                                     @RequestParam("deleted") String deleted,
                                     @RequestParam("combat_rating") String combatRating);

    default CensusCharacterList getCharacter(String name, String worldId, boolean wildcard) {
        if (wildcard) {
            return getCharacter(
                    "*" + name,
                    "false",
                    worldId,
                    "type:guild_roster^on:character_id^list:1",
                    "name,world_id,character_id,skill_points,combat_rating,pvp_combat_rating,gender_id,power_type_id,alignment_id,personality_id,movement_mode_id,max_health,max_power,defense,toughness,might,precision,restoration,vitalization,dominance",
                    "1",
                    "true",
                    "false",
                    "]1"
            );
        }

        return getCharacter(
                name,
                "false",
                worldId,
                "type:guild_roster^on:character_id^list:1",
                "name,world_id,character_id,skill_points,combat_rating,pvp_combat_rating,gender_id,power_type_id,alignment_id,personality_id,movement_mode_id,max_health,max_power,defense,toughness,might,precision,restoration,vitalization,dominance",
                "1",
                "true",
                "false",
                "]1"
        );
    }

    @GetMapping(value = "/get/dcuo/guild", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusCharacterGuildList getCharacterGuild(@RequestParam("guild_id") String guildId);

    @GetMapping(value = "/get/dcuo/characters_item", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusCharacterItemList getCharacterItems(@RequestParam("character_id") String characterId,
                                              @RequestParam("equipment_slot_id") String equipmentSlotId,
                                              @RequestParam("c%3Asort") String cSort,
                                              @RequestParam("c%3Alimit") String cLimit,
                                              @RequestParam("c%3Ashow") String cShow);

    default CensusCharacterItemList getCharacterArtifacts(String characterId) {
        return getCharacterItems(
                characterId,
                "]22",
                "equipment_slot_id",
                "999",
                "character_id,equipment_slot_id,item_id"
        );
    }

    default CensusCharacterItemList getCharacterAllies(String characterId) {
        return getCharacterItems(
                characterId,
                "]31",
                "equipment_slot_id",
                "999",
                "character_id,equipment_slot_id,item_id"
        );
    }

    @GetMapping(value = "/get/dcuo/character", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusCharacterGenderList getCharacterGender(@RequestParam("character_id") String characterId,
                                                 @RequestParam("c%3Ashow") String cShow);

    default CensusCharacterGenderList getCharacterGender(String characterId) {
        return getCharacterGender(characterId, "character_id,gender_id");
    }

    @GetMapping(value = "/get/dcuo/character", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusCharacterList getCharacterRanking(@RequestParam("world_id") String worldId,
                                            @RequestParam("c%3Asort") String cSort,
                                            @RequestParam("level") String level,
                                            @RequestParam("c%3Alimit") String cLimit,
                                            @RequestParam("skill_points") String skillPoints,
                                            @RequestParam("deleted") String deleted,
                                            @RequestParam("c%3Ashow") String cShow);

    default CensusCharacterList getCharacterRanking(String worldId, String sort) {
        return getCharacterRanking(
                worldId == null ? "<5002" : worldId,
                sort + ":-1,name:1",
                "30",
                "100",
                "]100",
                "false",
                "name,gender_id,character_id,skill_points,combat_rating,pvp_combat_rating,world_id,alignment_id,max_health,max_power,defense,dominance,might,precision,restoration,vitalization,power_type_id,movement_mode_id,personality_id,toughness"
        );
    }

    @GetMapping(value = "/get/dcuo/guild", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusGuildList getGuild(@RequestParam("name") String name,
                             @RequestParam("c%3Acase") String cCase,
                             @RequestParam("world_id") String worldId,
                             @RequestParam("c%3AexactMatchFirst") String cExactMatchFirst,
                             @RequestParam("c%3Alimit") String cLimit,
                             @RequestParam("c%3Ashow") String cShow);

    default CensusGuildList getGuild(String name, String worldId) {
        return getGuild(
                name,
                "false",
                worldId,
                "true",
                "1",
                "guild_id,name,world_id,character_alignment_id"
        );
    }

    @GetMapping(value = "/get/dcuo/guild_roster", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusGuildRosterList getGuildRoster(@RequestParam("c%3Alimit") String cLimit,
                                         @RequestParam("c%3Asort") String cSort,
                                         @RequestParam("c%3Ajoin") String cJoin,
                                         @RequestParam("on") String on,
                                         @RequestParam("guild_id") String guildId);

    default CensusGuildRosterList getGuildRoster(String guildId) {
        return getGuildRoster(
                "999",
                "rank",
                "character",
                "character_id",
                guildId
        );
    }

    @GetMapping(value = "/get/global/game_server_status", produces = MediaType.APPLICATION_JSON_VALUE)
    CensusGameServerStatusList getGameServerStatus(@RequestParam("c%3Alimit") String cLimit,
                                                   @RequestParam("game_code") String gameCode,
                                                   @RequestParam("c%3Asort") String cSort);

    default CensusGameServerStatusList getGameServerStatus() {
        return getGameServerStatus(
                "7",
                "dcuo",
                "name"
        );
    }
}
