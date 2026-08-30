package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.GenderResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "genders")
@Getter
@Setter
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    private String imageUrl;

    public static Gender fromResource(GenderResource resource) {
        Gender gender = new Gender();
        gender.setCensusId(resource.getId());
        gender.setName(resource.getName());
        gender.setImageUrl(resource.getImageUrl());
        return gender;
    }
}
