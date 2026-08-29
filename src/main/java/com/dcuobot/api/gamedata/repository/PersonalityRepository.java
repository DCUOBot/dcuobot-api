package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.Personality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalityRepository extends JpaRepository<Personality, Long> {
    Optional<Personality> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
