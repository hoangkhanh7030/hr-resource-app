package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity,Integer> {
    @Query(value = "select t from TeamEntity t where t.name=:name and t.workspaceEntityTeam.id=:idWorkspace ")
    Optional<TeamEntity> findByNameAndidWorkspace(@Param("name") String name,
                                                  @Param("idWorkspace") Integer idWorkspace);
    @Query(value = "select t from TeamEntity t where t.name=:name and t.workspaceEntityTeam.id=:idWorkspace " +
            "and t.id=:idTeam")
    Optional<TeamEntity> findByNameAndidWorkspaceAndIdTeam(@Param("idTeam") Integer idTeam,
                                                           @Param("name") String name,
                                                           @Param("idWorkspace") Integer idWorkspace);
    @Query(value = "select t from TeamEntity t where t.workspaceEntityTeam.id=:idWorkspace")
    List<TeamEntity> findAllByidWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select t from TeamEntity t where t.workspaceEntityTeam.id=:idWorkspace AND t.isArchived = false")
    List<TeamEntity> findAllActiveByWorkspaceId(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select t from TeamEntity t where t.workspaceEntityTeam.id=:idWorkspace and t.id=:idTeam")
    Optional<TeamEntity> findByidWorkspaceAndIdTeam(@Param("idWorkspace") Integer idWorkspace,
                                                    @Param("idTeam") Integer idTeam);

}
