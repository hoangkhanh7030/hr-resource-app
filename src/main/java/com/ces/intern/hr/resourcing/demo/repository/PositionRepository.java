package com.ces.intern.hr.resourcing.demo.repository;


import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionEntity, Integer> {
    Optional<PositionEntity> findByName(String name);
}
