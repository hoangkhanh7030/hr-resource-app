package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRepository extends JpaRepository<TimeEntity,Integer> {

    @Query(value = "select t from TimeEntity t where t.projectEntity.id=:idProject")
    List<TimeEntity> findAllByIdProject(@Param("idProject") Integer idProject);

//    @Query(value = "SELECT * FROM `time` where extract (YEAR from start_time)= :year AND extract (MONTH from start_time)" +
//            " = :month AND extract (DAY from start_time) = :day AND id = :resourceId", nativeQuery = true)
//    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") int year, @Param("month") int month,
//                                                   @Param("day") int day, @Param("resourceId") int id);

    @Query(value = "SELECT * FROM booking where date_part('year', start_time)= :year AND date_part('month', start_time)" +
            " = :month AND date_part('day', start_time) = :day AND resource_id = :resourceId", nativeQuery = true)
    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") Integer year, @Param("month") Integer month,
                                                   @Param("day") Integer day, @Param("resourceId") Integer id);

    @Query(value = "SELECT * FROM booking where date_part('year', start_time)= :year AND date_part('month', start_time)" +
            " = :month AND date_part('day', start_time) = :day AND id = :resourceId AND id != :timeId ", nativeQuery = true)
    Optional<List<TimeEntity>> findAllDifferentShiftOfResource(@Param("year") Integer year, @Param("month") Integer month,
                                                               @Param("day") Integer day, @Param("resourceId") Integer id,
                                                               @Param("timeId") Integer timeId);

//    @Query(value = "SELECT b.* from booking b\n" +
//            "    inner join resource r on b.resource_id = r.id\n" +
//            "    inner join workspace w on r.position_id = w.id\n" +
//            "where date_part('year', start_time) = :year and date_part('month', start_time) = :month and date_part('day', start_time) = :day \n" +
//            "and r.workspace_id = :workspaceId", nativeQuery = true)
//    Optional<List<TimeEntity>> findAllShiftOfMonth(@Param("year") Integer year, @Param("month") Integer month,
//                                                   @Param("day") Integer day, @Param("workspaceId") Integer workspaceId);
    @Query(value = "select t from TimeEntity t where t.resourceEntity.workspaceEntityResource.id=:idWorkspace")
    List<TimeEntity> findAllByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select  t from  TimeEntity  t where t.resourceEntity.id=:idResource")
    List<TimeEntity> findAllByIdResource(@Param("idResource") Integer idResource);

    @Query(value = "select * from resource r left join booking b on r.id = b.resource_id " +
            "left join project p on b.project_id = p.id left join team t on r.team_id = t.id " +
            "left join position po on r.position_id=po.id where r.workspace_id =:idWorkspace and" +
            " (lower(r.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(t.name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(p.name) like lower(concat('%',:searchName,'%'))" +
            "OR lower(p.client_name) like lower(concat('%',:searchName,'%')) " +
            "OR lower(po.name) like lower(concat('%',:searchName,'%')))", nativeQuery = true)
    List<TimeEntity> findAllBySearchName(@Param("idWorkspace") Integer idWorkspace,
                                         @Param("searchName") String searchName);
}
