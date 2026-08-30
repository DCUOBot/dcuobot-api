package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenderRepository extends JpaRepository<Gender, Long> {
    Optional<Gender> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
