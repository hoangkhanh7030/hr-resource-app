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

    @Query(value = "select rc from ResourceEntity rc where rc.positionEntity.teamEntity.workspaceEntityTeam.id=:idWorkspace" +
            " AND  rc.positionEntity.name=:namePosition")
    List<ResourceEntity> findAllByIdWorkspaceAndNamePosition(@Param("idWorkspace") Integer idWorkspace,
                                                             @Param("namePosition") String namePosition);

    @Query(value = "SELECT res FROM ResourceEntity res WHERE res.name LIKE concat('%', :name, '%') ")
    List<ResourceEntity> search(@Param("name") String name);


    @Query(value = "SELECT res FROM ResourceEntity res WHERE res.positionEntity.teamEntity.workspaceEntityTeam.id = :id")
    Page<ResourceEntity> findResourcesOfWorkSpace(@Param("id") Integer id, Pageable pageable);


    //Optional<ResourceEntity> findByIdAndPositionEntityTeamEntityWorkspaceEntityId(Integer resourceId, Integer workspaceId);


    Optional<ResourceEntity> findByIdAndPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(Integer resourceId, Integer workspaceId);


    @Query("select r from ResourceEntity r where lower(r.name) LIKE lower(concat('%',:searchName,'%')) AND lower(r.positionEntity.name) LIKE lower(concat('%',:posName,'%')) " +
            "AND lower(r.positionEntity.teamEntity.name) LIKE lower(concat('%',:teamName,'%')) AND r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId")
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


    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId" +
            " AND r.positionEntity.teamEntity.name = :teamName")
    Page<ResourceEntity> filterByTeam(@Param("workspaceId") Integer workspaceId,
                                      @Param("teamName") String teamName,
                                      Pageable pageable);

    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId" +
            " AND r.positionEntity.name = :posName")
    Page<ResourceEntity> filterByPosition(@Param("workspaceId") Integer workspaceId,
                                          @Param("posName") String posName,
                                          Pageable pageable);

    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId" +
            " AND r.positionEntity.teamEntity.name = :teamName AND r.positionEntity.name = :posName")
    Page<ResourceEntity> filterByTeamAndPosition(@Param("workspaceId") Integer workspaceId,
                                                 @Param("teamName") String teamName,
                                                 @Param("posName") String posName,
                                                 Pageable pageable);

    Optional<ResourceEntity> findByPositionEntity_TeamEntity_IdAndId(Integer idTeam, Integer idResource);

    Optional<ResourceEntity> findByPositionEntity_TeamEntity_WorkspaceEntityTeam_IdAndId(Integer workspaceId,Integer idResource);


    @Query("select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId AND" +
            " lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "AND lower(r.positionEntity.teamEntity.name) like lower(concat('%',:teamName,'%')) AND lower(r.positionEntity.name) " +
            "like lower(concat('%',:posName,'%'))")
    Page<ResourceEntity> filterList(@Param("workspaceId") Integer workspaceId,
                                    @Param("searchName") String searchName,
                                    @Param("teamName") String teamName,
                                    @Param("posName") String posName,
                                    Pageable pageable);

    @Query("select count(r) from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id = :workspaceId AND" +
            " lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "AND lower(r.positionEntity.teamEntity.name) like lower(concat('%',:teamName,'%')) AND lower(r.positionEntity.name) " +
            "like lower(concat('%',:posName,'%'))")
    Integer getNumberOfResourcesOfWorkspace(@Param("workspaceId") Integer workspaceId,
                                            @Param("searchName") String searchName,
                                            @Param("teamName") String teamName,
                                            @Param("posName") String posName);

    List<ResourceEntity> findAllByPositionEntity_TeamEntity_WorkspaceEntityTeam_Id(Integer workspaceId);

    @Query(value = "select r from ResourceEntity r where r.positionEntity.teamEntity.workspaceEntityTeam.id =:idWorkspace")
    List<ResourceEntity> findAllByidWorkspace(@Param("idWorkspace") Integer idWorkspace);


}
