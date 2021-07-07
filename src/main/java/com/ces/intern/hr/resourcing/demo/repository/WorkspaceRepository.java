package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity,Integer> {


     Optional<WorkspaceEntity> findById(int id);

     Optional<WorkspaceEntity> findByName(String name);

     List<WorkspaceEntity> findAllByNameContainingIgnoreCase(String name);


}
