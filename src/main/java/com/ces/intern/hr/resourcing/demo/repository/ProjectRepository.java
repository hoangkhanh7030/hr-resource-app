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

    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and (lower(p.name) " +
            "like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')))")
    List<ProjectEntity> findAllByidWorkspaceAndSearchName(@Param("idWorkspace") Integer idWorkspace,
                                                    @Param("name") String name);

    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and p.isActivate=:isActivate and (lower(p.name) " +
            "like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')))")
   List<ProjectEntity> findAllByNameAndClientNameAndActivate(@Param("idWorkspace") Integer idWorkspace,
                                                                @Param("name") String name,
                                                                @Param("isActivate") Boolean isActivate
                                                                );


    List<ProjectEntity> findAllByWorkspaceEntityProject_Id(Integer idWorkspace);

    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and p.isActivate=:isActivate and (lower(p.name) " +
            "like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')))")
    Page<ProjectEntity> findAllByNameAndClientNameAndIsActivate(@Param("idWorkspace") Integer idWorkspace,
                                                                @Param("name") String name,
                                                                @Param("isActivate") Boolean isActivate,
                                                                Pageable pageable);


    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and (lower(p.name) " +
            "like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')))")
    Page<ProjectEntity> findAllByNameAndClientName(@Param("idWorkspace") Integer idWorkspace,
                                                   @Param("name") String name,
                                                   Pageable pageable);

    Optional<ProjectEntity> findByName(String name);


    Optional<ProjectEntity> findByIdAndWorkspaceEntityProject_Id(Integer idProject,Integer idWorkspace);



    @Query(value = "select p from ProjectEntity p where p.workspaceEntityProject.id=:idWorkspace and p.isActivate=true and (lower(p.name) " +
            "like lower(concat('%',:name,'%')) or lower(p.clientName) like lower(concat('%',:name,'%')))")
    List<ProjectEntity> findAll(@Param("idWorkspace") Integer idWorkspace,
                                                                @Param("name") String name);

}
