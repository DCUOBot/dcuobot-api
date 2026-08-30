package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.AllyResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allies")
@Getter
@Setter
public class Ally {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String censusId;

    public static Ally fromResource(AllyResource resource) {
        Ally ally = new Ally();
        ally.setCensusId(resource.getId());
        ally.setName(resource.getName());
        return ally;
    }
}
