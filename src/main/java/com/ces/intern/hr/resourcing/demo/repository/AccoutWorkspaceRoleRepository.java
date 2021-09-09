package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccoutWorkspaceRoleRepository extends JpaRepository<AccountWorkspaceRoleEntity, Integer> {
    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.id=:idWorkspace " +
            "and ac.accountEntity.id=:idAccount and ac.accountEntity.authenticationProvider='GOOGLE'")
    Optional<AccountWorkspaceRoleEntity> findByIdAndId(@Param("idWorkspace") Integer idWorkspace, @Param("idAccount") Integer idAccount);

    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.id=:idWorkspace")
    Optional<AccountWorkspaceRoleEntity> findByIdWorkspace(@Param("idWorkspace") Integer idWorkspace);

    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.name=:nameWorkspace")
    Optional<AccountWorkspaceRoleEntity> findByNameWorkspace(@Param("nameWorkspace") String nameWorkspace);
    @Query(value = "select ac from AccountWorkspaceRoleEntity ac WHERE ac.workspaceEntity.name=:nameWorkspace and ac.accountEntity.id=:idAccount")
    Optional<AccountWorkspaceRoleEntity> findByNameWorkspaceAndIdAccount(@Param("nameWorkspace") String nameWorkspace,@Param("idAccount") Integer idAccount);

    List<AccountWorkspaceRoleEntity> findAllByAccountEntity_Id(Integer idAccount);
    @Query("select ac from AccountWorkspaceRoleEntity ac where ac.workspaceEntity.id=:idWorkspace and ac.codeRole=1")
    Page<AccountWorkspaceRoleEntity> findAllByWorkspaceEntity_Id(@Param("idWorkspace") Integer idWorkspace, Pageable pageable);
}
