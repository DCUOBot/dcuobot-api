package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.Alignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlignmentRepository extends JpaRepository<Alignment, Long> {
    Optional<Alignment> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
