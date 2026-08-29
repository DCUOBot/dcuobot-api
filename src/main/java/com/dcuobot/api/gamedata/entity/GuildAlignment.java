package com.dcuobot.api.gamedata.entity;

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
}
