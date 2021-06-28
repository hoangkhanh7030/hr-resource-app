package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;



public interface TimeRepository extends JpaRepository<TimeEntity,Integer> {

    @Query(value = "select t from TimeEntity t where t.projectEntity.id=:idProject")
    List<TimeEntity> findAllByIdProject(@Param("idProject") Integer idProject);

}
