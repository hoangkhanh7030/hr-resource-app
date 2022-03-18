package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, Integer> {
    @Query("select w from WorkspaceEntity w where w.id=:idWorkspace")
    Optional<WorkspaceEntity> findByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    List<WorkspaceEntity> findAllByNameContainingIgnoreCase(String name);

    @Query("select w from WorkspaceEntity w where w.name =:name and w.createdBy =:id")
    Optional<WorkspaceEntity> findByNameWorkspaceAndId(@Param("name") String name,
                                                       @Param("id") Integer id);


}
