package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.PowerTypeResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "power_types")
@Getter
@Setter
public class PowerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    public static PowerType fromResource(PowerTypeResource resource) {
        PowerType powerType = new PowerType();
        powerType.setCensusId(resource.getId());
        powerType.setName(resource.getName());
        return powerType;
    }
}
