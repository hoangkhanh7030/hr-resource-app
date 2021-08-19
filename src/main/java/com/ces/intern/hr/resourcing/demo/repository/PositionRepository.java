package com.ces.intern.hr.resourcing.demo.repository;


import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Integer> {
    Optional<PositionEntity> findByNameAndTeamEntity_Id(String name,Integer idTeam);
    List<PositionEntity> findAllByTeamEntity_Id(Integer idTeam);
    @Query(value = "select p from PositionEntity p where p.teamEntity.workspaceEntityTeam.id=:idWorkspace")
    List<PositionEntity> findAllByidWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select p from PositionEntity p where p.teamEntity.workspaceEntityTeam.id=:idWorkspace and " +
            "p.teamEntity.id=:idTeam")
    List<PositionEntity> findAllByidWorkspaceAndidTeam(@Param("idWorkspace") Integer idWorkspace,
                                                       @Param("idTeam") Integer idTeam);

    @Query(value = "select p from PositionEntity p where p.teamEntity.workspaceEntityTeam.id=:idWorkspace and " +
            "p.teamEntity.id=:idTeam and p.name=:name")
    Optional<PositionEntity> findByidWorkspaceAndidTeam(@Param("idWorkspace") Integer idWorkspace,
                                                        @Param("idTeam") Integer idTeam,
                                                        @Param("name") String name);

    @Query(value = "select p from PositionEntity p where p.teamEntity.workspaceEntityTeam.id=:idWorkspace and p.isArchived = false")
    List<PositionEntity> findAllActiveByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select p from PositionEntity p where p.teamEntity.workspaceEntityTeam.id=:idWorkspace and " +
            "p.teamEntity.id=:idTeam AND p.teamEntity.isArchived = false")
    List<PositionEntity> findAllActiveByIdWorkspaceAndIdTeam(@Param("idWorkspace") Integer idWorkspace,
                                                             @Param("idTeam") Integer idTeam);

}
