package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.Ally;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllyRepository extends JpaRepository<Ally, Long> {
    Optional<Ally> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
