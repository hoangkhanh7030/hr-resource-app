package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<ResourceEntity,Integer> {

    @Query(value = "select rc from ResourceEntity rc where rc.workspaceEntityResource.id=:idWorkspace" +
            " AND  rc.positionEntity.name=:namePosition")
    List<ResourceEntity> findAllByIdWorkspaceAndNamePosition(@Param("idWorkspace") Integer idWorkspace,
                                                             @Param("namePosition") String namePosition);

    Optional<ResourceEntity> findByName(String name);

    @Query("SELECT res FROM ResourceEntity res WHERE res.name LIKE concat('%', :name, '%') ")
    List<ResourceEntity> search(@Param("name") String name);

    List<ResourceEntity> findByNameContainingIgnoreCaseAndWorkspaceEntityResource_Id(String name, Integer id);

    @Query("SELECT res FROM ResourceEntity res WHERE res.workspaceEntityResource.id = :id")
    List<ResourceEntity> findResourcesOfWorkSpace(@Param("id") int id);

    Optional<ResourceEntity> findByIdAndWorkspaceEntityResource_Id(int id, int workspaceId);

    @Query("SELECT res from ResourceEntity res where res.positionEntity.id = 1 and res.workspaceEntityResource.id = :id")
    List<ResourceEntity> findAllProductManagersOfWorkspace(@Param("id") int id);

    @Query("SELECT res from ResourceEntity res where res.positionEntity.id = 2 and res.workspaceEntityResource.id = :id")
    List<ResourceEntity> findAllAccountManagersOfWorkspace(@Param("id") int id);

    @Query("select res from ResourceEntity res where res.workspaceEntityResource.id=:idWorkspace")
    List<ResourceEntity> findAllByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query("select res from ResourceEntity res where res.teamEntity.id=:teamId and res.id=:idResource")
    Optional<ResourceEntity> findByIdTeamandIdResource(@Param("teamId") Integer teamId,@Param("idResource") Integer idResource);
}
