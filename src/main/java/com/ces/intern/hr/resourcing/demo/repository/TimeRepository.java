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
            " = :month AND date_part('day', start_time) = :day AND id = :resourceId", nativeQuery = true)
    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") int year, @Param("month") int month,
                                                   @Param("day") int day, @Param("resourceId") int id);

    @Query(value = "SELECT b.* from booking b\n" +
            "    inner join resource r on b.resource_id = r.id\n" +
            "    inner join workspace w on r.workspace_id = w.id\n" +
            "where date_part('year', start_time) = :year and date_part('month', start_time) = :month and date_part('day', start_time) = :day \n" +
            "and r.workspace_id = :workspaceId", nativeQuery = true)
    Optional<List<TimeEntity>> findAllShiftOfMonth(@Param("year") int year, @Param("month") int month,
                                                   @Param("day") int day, @Param("workspaceId") int workspaceId);

}
