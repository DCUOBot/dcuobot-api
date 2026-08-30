package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.PersonalityResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "personalities")
@Getter
@Setter
public class Personality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    public static Personality fromResource(PersonalityResource resource) {
        Personality personality = new Personality();
        personality.setCensusId(resource.getId());
        personality.setName(resource.getName());
        return personality;
    }
}
