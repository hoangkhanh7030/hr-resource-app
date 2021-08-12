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


    @Query(value = "SELECT res FROM ResourceEntity res WHERE res.workspaceEntityResource.id = :id")
    Page<ResourceEntity> findResourcesOfWorkSpace(@Param("id") Integer id, Pageable pageable);


    //Optional<ResourceEntity> findByIdAndPositionEntityTeamEntityWorkspaceEntityId(Integer resourceId, Integer workspaceId);


    Optional<ResourceEntity> findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(Integer resourceId, Integer workspaceId);


    @Query("select r from ResourceEntity r where lower(r.name) LIKE lower(concat('%',:searchName,'%')) AND lower(r.positionEntity.name) LIKE lower(concat('%',:posName,'%')) " +
            "AND lower(r.positionEntity.teamEntity.name) LIKE lower(concat('%',:teamName,'%')) AND r.workspaceEntityResource.id = :workspaceId")
    Page<ResourceEntity> filterResultByParameter(@Param("searchName") String name,
                                                 @Param("posName") String posName,
                                                 @Param("teamName") String teamName,
                                                 @Param("workspaceId") Integer workspaceId,
                                                 Pageable pageable);

//    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntity.id = :workspaceId" +
//            " AND r.positionEntity.teamEntity.name = :teamName")
//    Page<ResourceEntity> filterByTeam(@Param("workspaceId") Integer workspaceId,
//                                      @Param("teamName") String teamName,
//                                      Pageable pageable);
//
//    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntity.id = :workspaceId" +
//            " AND r.positionEntity.name = :posName")
//    Page<ResourceEntity> filterByPosition(@Param("workspaceId") Integer workspaceId,
//                                          @Param("posName") String posName,
//                                          Pageable pageable);
//

//    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntity.id = :workspaceId" +
//            " AND r.positionEntity.teamEntity.name = :teamName AND r.positionEntity.name = :posName")
//    Page<ResourceEntity> filterByTeamAndPosition(@Param("workspaceId") Integer workspaceId,
//                                                 @Param("teamName") String teamName,
//                                                 @Param("posName") String posName,
//                                                 Pageable pageable);


    //Optional<ResourceEntity> findByPositionEntityTeamEntity_IdAndId(Integer idTeam,Integer idResource);


    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId" +
            " AND r.positionEntity.teamEntity.name = :teamName")
    Page<ResourceEntity> filterByTeam(@Param("workspaceId") Integer workspaceId,
                                      @Param("teamName") String teamName,
                                      Pageable pageable);

    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId" +
            " AND r.positionEntity.name = :posName")
    Page<ResourceEntity> filterByPosition(@Param("workspaceId") Integer workspaceId,
                                          @Param("posName") String posName,
                                          Pageable pageable);

    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId" +
            " AND r.positionEntity.teamEntity.name = :teamName AND r.positionEntity.name = :posName")
    Page<ResourceEntity> filterByTeamAndPosition(@Param("workspaceId") Integer workspaceId,
                                                 @Param("teamName") String teamName,
                                                 @Param("posName") String posName,
                                                 Pageable pageable);

    Optional<ResourceEntity> findByPositionEntity_TeamEntity_IdAndId(Integer idTeam, Integer idResource);

    Optional<ResourceEntity> findByWorkspaceEntityResource_IdAndId(Integer workspaceId,Integer idResource);


//    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId AND" +
//            " lower(r.name) like lower(concat('%',:searchName,'%')) " +
//            "AND lower(r.positionEntity.teamEntity.name) like lower(concat('%',:teamName,'%')) AND lower(r.positionEntity.name) " +
//            "like lower(concat('%',:posName,'%'))")
//    Page<ResourceEntity> filterList(@Param("workspaceId") Integer workspaceId,
//                                    @Param("searchName") String searchName,
//                                    @Param("teamName") String teamName,
//                                    @Param("posName") String posName,
//                                    Pageable pageable);


    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId AND " +
            "(lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(r.positionEntity.teamEntity.name) like lower(concat('%',:searchName,'%')) OR lower(r.positionEntity.name) " +
            "like lower(concat('%',:searchName,'%')))")
    Page<ResourceEntity> filterList(@Param("workspaceId") Integer workspaceId,
                                    @Param("searchName") String searchName,
                                    Pageable pageable);




    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId AND r.isArchived = :isArchived AND " +
            " (lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(r.teamEntityResource.name) like lower(concat('%',:searchName,'%')) OR lower(r.positionEntity.name) " +
            "like lower(concat('%',:searchName,'%'))) ")
    Page<ResourceEntity> filterListByStatus(@Param("workspaceId") Integer workspaceId,
                                            @Param("searchName") String searchName,
                                            @Param("isArchived") Boolean isArchived,
                                            Pageable pageable);
    @Query("select r from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId AND r.isArchived =false AND lower(r.name) like lower(concat('%',:searchName,'%'))")
    List<ResourceEntity> findAll(@Param("workspaceId") Integer workspaceId,
                                @Param("searchName") String searchName);


//    @Query("select count(r) from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId AND" +
//            " lower(r.name) like lower(concat('%',:searchName,'%')) " +
//            "AND lower(r.positionEntity.teamEntity.name) like lower(concat('%',:teamName,'%')) AND lower(r.positionEntity.name) " +
//            "like lower(concat('%',:posName,'%'))")
//    Integer getNumberOfResourcesOfWorkspace(@Param("workspaceId") Integer workspaceId,
//                                            @Param("searchName") String searchName,
//                                            @Param("teamName") String teamName,
//                                            @Param("posName") String posName);


    @Query("select count(r) from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId AND" +
            " (lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(r.positionEntity.teamEntity.name) like lower(concat('%',:searchName,'%')) OR lower(r.positionEntity.name) " +
            "like lower(concat('%',:searchName,'%')))")
    Integer getNumberOfResourcesOfWorkspace(@Param("workspaceId") Integer workspaceId,
                                            @Param("searchName") String searchName);

    @Query("select count(r) from ResourceEntity r where r.workspaceEntityResource.id = :workspaceId AND" +
            " (lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(r.positionEntity.teamEntity.name) like lower(concat('%',:searchName,'%')) OR lower(r.positionEntity.name) " +
            "like lower(concat('%',:searchName,'%'))) AND r.isArchived = :isArchived")
    Integer getNumberOfResourcesOfWorkspaceWithStatus(@Param("workspaceId") Integer workspaceId,
                                                      @Param("isArchived") Boolean isArchived,
                                                      @Param("searchName") String searchName);

    List<ResourceEntity> findAllByPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(Integer workspaceId);
    List<ResourceEntity> findAllByWorkspaceEntityResource_Id(Integer id);

    @Query(value = "select r from ResourceEntity r where r.workspaceEntityResource.id =:idWorkspace")
    List<ResourceEntity> findAllByidWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select r from  ResourceEntity r where r.workspaceEntityResource.id=:idWorkspace")
    Page<ResourceEntity> findAllid(@Param("idWorkspace") Integer idWorkspace,Pageable pageable);

    @Query(value = "select * from resource r left join booking b on r.id = b.resource_id " +
            "left join project p on b.project_id = p.id left join team t on r.team_id = t.id " +
            "left join position po on r.position_id=po.id where r.workspace_id =:idWorkspace and" +
            " (lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(t.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(p.name) like lower(concat('%',:searchName,'%'))" +
            "OR lower(p.client_name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(po.name) like lower(concat('%',:searchName,'%')))",nativeQuery = true)
    List<ResourceEntity> findAllBySearchName(@Param("idWorkspace") Integer idWorkspace,
                                             @Param("searchName") String searchName);

}
