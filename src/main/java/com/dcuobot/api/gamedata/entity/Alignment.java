package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.AlignmentResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "alignments")
@Getter
@Setter
public class Alignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    public static Alignment fromResource(AlignmentResource resource) {
        Alignment alignment = new Alignment();
        alignment.setCensusId(resource.getId());
        alignment.setName(resource.getName());
        return alignment;
    }
}
