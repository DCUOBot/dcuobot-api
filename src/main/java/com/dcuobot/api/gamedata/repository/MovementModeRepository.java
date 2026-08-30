package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.MovementMode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovementModeRepository extends JpaRepository<MovementMode, Long> {
    Optional<MovementMode> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
