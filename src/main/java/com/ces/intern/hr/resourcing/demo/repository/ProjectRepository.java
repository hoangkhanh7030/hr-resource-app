package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity,Integer> {

    Optional<ProjectEntity> findByNameAndWorkspaceEntityProject_Id(String name,Integer id);
    @Query("select p from ProjectEntity p where p.workspaceEntityProject.id =:idworkspace")
    Page<ProjectEntity> findAllById(@Param("idworkspace") Integer idworkspace, Pageable pageable);

    @Query("select p from ProjectEntity p where p.workspaceEntityProject.id =:idworkspace")
    List<ProjectEntity> findAllByidWorkspace(@Param("idworkspace") Integer idworkspace);

    @Query("select p from ProjectEntity p where p.id=:idProject and p.workspaceEntityProject.id=:idWorkspace")
    Optional<ProjectEntity> findByIdWorkspaceAndIdProject(@Param("idWorkspace") Integer idWorkspace,@Param("idProject") Integer idProject);

    @Query("select p from ProjectEntity p where p.isActivate=:activate and p.id=:idProject")
    Optional<ProjectEntity> findByIdAndIsActivate(@Param("activate") boolean activate,@Param("idProject") Integer idProject);


    List<ProjectEntity> findAllByWorkspaceEntityProject_Id(Integer idWorkspace);

    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and lower(p.name) like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')) " +
            "and p.isActivate=:isActivate")
    Page<ProjectEntity> findAllByNameAndClientNameAndIsActivate(@Param("idWorkspace") Integer idWorkspace,
                                                                @Param("name") String name,
                                                                @Param("isActivate") Boolean isActivate,
                                                                Pageable pageable);
    Optional<ProjectEntity> findByName(String name);
    Optional<ProjectEntity> findByIdAndWorkspaceEntityProject_Id(Integer idProject,Integer idWorkspace);



}
