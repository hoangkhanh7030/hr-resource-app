package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface TimeRepository extends JpaRepository<TimeEntity,Integer> {

    @Query(value = "select t from TimeEntity t where t.projectEntity.id=:idProject")
    List<TimeEntity> findAllByIdProject(@Param("idProject") Integer idProject);

//    @Query(value = "SELECT * FROM `time` where extract (YEAR from start_time)= :year AND extract (MONTH from start_time)" +
//            " = :month AND extract (DAY from start_time) = :day AND id = :resourceId", nativeQuery = true)
//    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") int year, @Param("month") int month,
//                                                   @Param("day") int day, @Param("resourceId") int id);

    @Query(value = "SELECT * FROM `time` where year (start_time)= :year AND month (start_time)" +
            " = :month AND day (start_time) = :day AND id = :resourceId", nativeQuery = true)
    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") int year, @Param("month") int month,
                                                   @Param("day") int day, @Param("resourceId") int id);

}
