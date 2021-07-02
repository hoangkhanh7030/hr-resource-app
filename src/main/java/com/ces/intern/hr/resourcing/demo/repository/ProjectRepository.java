package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer> {
    Optional<ProjectEntity> findByName(String name);
    @Query("select p from ProjectEntity p where p.workspaceEntityProject.id =:idworkspace")
    List<ProjectEntity> findAllById(@Param("idworkspace") Integer idworkspace);

    @Query("select p from ProjectEntity p where p.id=:idProject and p.workspaceEntityProject.id=:idWorkspace")
    Optional<ProjectEntity> findByIdWorkspaceAndIdProject(@Param("idWorkspace") Integer idWorkspace,@Param("idProject") Integer idProject);

    @Query("select p from ProjectEntity p where p.isActivate=:activate and p.id=:idProject")
    Optional<ProjectEntity> findByIdAndIsActivate(@Param("activate") boolean activate,@Param("idProject") Integer idProject);

    List<ProjectEntity> findAllByNameContainingIgnoreCase(String name);
}
