package com.dcuobot.api.character.dto;

import com.dcuobot.api.gamedata.entity.Ally;
import lombok.Data;

@Data
public class CharacterAllyResponse {
    private String id;

    private String name;

    private boolean combat;

    public static CharacterAllyResponse fromEntity(Ally ally, boolean combat) {
        CharacterAllyResponse response = new CharacterAllyResponse();
        response.setId(ally.getCensusId());
        response.setName(ally.getName());
        response.setCombat(combat);
        return response;
    }
}
