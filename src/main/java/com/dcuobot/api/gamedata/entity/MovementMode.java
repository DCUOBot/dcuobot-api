package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.MovementModeResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movement_modes")
@Getter
@Setter
public class MovementMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    public static MovementMode fromResource(MovementModeResource resource) {
        MovementMode movementMode = new MovementMode();
        movementMode.setCensusId(resource.getId());
        movementMode.setName(resource.getName());
        return movementMode;
    }
}
