package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccoutWorkspaceRoleRepository extends JpaRepository<AccountWorkspaceRoleEntity, Integer> {
    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.id=:idWorkspace " +
            "and ac.accountEntity.id=:idAccount")
    Optional<AccountWorkspaceRoleEntity> findByIdAndId(@Param("idWorkspace") Integer idWorkspace, @Param("idAccount") Integer idAccount);

    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.id=:idWorkspace")
    Optional<AccountWorkspaceRoleEntity> findByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.name=:nameWorkspace")
    Optional<AccountWorkspaceRoleEntity> findByNameWorkspace(@Param("nameWorkspace") String nameWorkspace);
    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.name=:nameWorkspace and ac.accountEntity.id=:idAccount")
    Optional<AccountWorkspaceRoleEntity> findByNameWorkspaceAndIdAccount(@Param("nameWorkspace") String nameWorkspace,@Param("idAccount") Integer idAccount);

    List<AccountWorkspaceRoleEntity> findAllByAccountEntity_Id(Integer idAccount);
}
