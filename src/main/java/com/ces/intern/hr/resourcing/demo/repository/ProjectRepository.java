package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer> {
    Optional<ProjectEntity> findByName(String name);
    @Query("select p from ProjectEntity p where p.workspaceEntityProject.id =:idworkspace")
    List<ProjectEntity> findAllById(@Param("idworkspace") Integer idworkspace);
}
