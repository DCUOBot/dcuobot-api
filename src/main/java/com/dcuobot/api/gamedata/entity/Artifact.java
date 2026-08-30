package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.ArtifactResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "artifacts")
@Getter
@Setter
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String censusId;

    private String imageUrl;

    private String discordEmojiId;

    public static Artifact fromResource(ArtifactResource resource) {
        Artifact artifact = new Artifact();
        artifact.setCensusId(resource.getId());
        artifact.setName(resource.getName());
        artifact.setImageUrl(resource.getImageUrl());
        artifact.setDiscordEmojiId(resource.getDiscordEmojiId());
        return artifact;
    }
}
