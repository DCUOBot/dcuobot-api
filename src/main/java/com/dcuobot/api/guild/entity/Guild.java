package com.dcuobot.api.guild.entity;

import com.dcuobot.api.gamedata.entity.GuildAlignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leagues")
@Getter
@Setter
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String censusId;

    private String name;

    @ManyToOne
    private GuildAlignment alignment;

    private String worldId;

    private int memberCount;

    private double averageSkillPoints;

    private double averageCombatRating;

    private double averagePvpCombatRating;
}
