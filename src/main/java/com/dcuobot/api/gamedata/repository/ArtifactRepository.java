package com.dcuobot.api.gamedata.repository;

import com.dcuobot.api.gamedata.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {
    Optional<Artifact> findByCensusId(String censusId);

    boolean existsByCensusId(String censusId);
}
