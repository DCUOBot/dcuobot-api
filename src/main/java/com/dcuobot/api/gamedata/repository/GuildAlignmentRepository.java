package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.GuildAlignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildAlignmentRepository extends JpaRepository<GuildAlignment, Long> {
    Optional<GuildAlignment> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
