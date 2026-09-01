package com.dcuobot.api.character.dto;

import com.dcuobot.api.gamedata.entity.Ally;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "An ally equipped by a character.")
public class CharacterAllyResponse {
    @Schema(description = "Census ally item id.")
    private String id;

    @Schema(description = "Ally name.")
    private String name;

    @Schema(description = "Whether this ally is equipped as the active combat ally (equipment slot 31), " +
            "as opposed to a passive/buff ally slot.")
    private boolean combat;

    public static CharacterAllyResponse fromEntity(Ally ally, boolean combat) {
        CharacterAllyResponse response = new CharacterAllyResponse();
        response.setId(ally.getCensusId());
        response.setName(ally.getName());
        response.setCombat(combat);
        return response;
    }
}
