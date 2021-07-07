package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer> {
    Optional<ProjectEntity> findByName(String name);
}
