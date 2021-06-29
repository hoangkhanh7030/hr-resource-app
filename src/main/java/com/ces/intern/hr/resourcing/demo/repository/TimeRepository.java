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

    @Query(value = "SELECT * FROM `time` where date_part('year', start_time) = :year AND date_part('month', start_time)" +
            " = :month AND date_part('day',start_time) = :day AND id = :resourceId", nativeQuery = true)
    Optional<List<TimeEntity>> findShiftOfResource(@Param("year") int year, @Param("month") int month,
                                                   @Param("day") int day, @Param("resourceId") int id);

}
