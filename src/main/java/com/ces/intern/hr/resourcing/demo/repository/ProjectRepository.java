package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer> {
    Optional<ProjectEntity> findByName(String name);

}
