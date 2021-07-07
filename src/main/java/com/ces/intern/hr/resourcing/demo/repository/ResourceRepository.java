package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity,Integer> {

    @Query(value = "select rc from ResourceEntity rc where rc.workspaceEntityResource.id=:idWorkspace" +
            " AND  rc.positionEntity.name=:namePosition")
    List<ResourceEntity> findAllByIdWorkspaceAndNamePosition(@Param("idWorkspace") Integer idWorkspace,
                                                             @Param("namePosition") String namePosition);

    @Query(value = "SELECT res FROM ResourceEntity res WHERE res.name LIKE concat('%', :name, '%') ")
    List<ResourceEntity> search(@Param("name") String name);

    Page<ResourceEntity> findByNameContainingIgnoreCaseAndWorkspaceEntityResource_Id(String name, Integer id, Pageable pageable);

    @Query(value = "SELECT res FROM ResourceEntity res WHERE res.workspaceEntityResource.id = :id")
    Page<ResourceEntity> findResourcesOfWorkSpace(@Param("id") Integer id, Pageable pageable);

    //Optional<ResourceEntity> findByIdAndWorkspaceEntityResource_Id(int id, int workspaceId);

    @Query(value = "SELECT res from ResourceEntity res where res.positionEntity.id = 1 and res.workspaceEntityResource.id = :id")
    List<ResourceEntity> findAllProductManagersOfWorkspace(@Param("id") Integer id);

    @Query(value = "SELECT res from ResourceEntity res where res.positionEntity.id = 2 and res.workspaceEntityResource.id = :id")
    List<ResourceEntity> findAllAccountManagersOfWorkspace(@Param("id") Integer id);

    Optional<ResourceEntity> findByIdAndWorkspaceEntityResource_Id(Integer resourceId, Integer workspaceId);
}
