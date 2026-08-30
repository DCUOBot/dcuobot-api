package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.PowerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PowerTypeRepository extends JpaRepository<PowerType, Long> {
    Optional<PowerType> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
