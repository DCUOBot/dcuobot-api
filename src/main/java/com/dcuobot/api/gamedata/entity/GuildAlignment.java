package com.dcuobot.api.gamedata.entity;

import com.dcuobot.api.gamedata.resource.GuildAlignmentResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "guild_alignments")
@Getter
@Setter
public class GuildAlignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    public static GuildAlignment fromResource(GuildAlignmentResource resource) {
        GuildAlignment guildAlignment = new GuildAlignment();
        guildAlignment.setCensusId(resource.getId());
        guildAlignment.setName(resource.getName());
        return guildAlignment;
    }
}
