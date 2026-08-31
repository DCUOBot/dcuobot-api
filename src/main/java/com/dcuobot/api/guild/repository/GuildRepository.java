package com.dcuobot.api.guild.repository;

import com.dcuobot.api.guild.entity.Guild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuildRepository extends JpaRepository<Guild, Long> {
    Optional<Guild> findByCensusId(String censusId);

    Page<Guild> findAllByMemberCountGreaterThanEqual(Pageable pageable, int memberCount);

    Page<Guild> findAllByWorldIdAndMemberCountGreaterThanEqual(Pageable pageable, String worldId, int memberCount);

    Boolean existsByNameAndWorldId(String name, String worldId);

    void deleteByNameAndWorldId(String name, String worldId);
}
