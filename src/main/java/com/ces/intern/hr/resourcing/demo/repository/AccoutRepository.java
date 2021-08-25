package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccoutRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity getAccountEntitiesByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);
    @Query("select a from AccountEntity a where a.id=:idAccount")
    Optional<AccountEntity> findById(@Param("idAccount") Integer idAccount);

    @Query(value = "select * from account a where a.email=:email and a.auth_provider='NOTHING'", nativeQuery = true)
    Optional<AccountEntity> findByEmailAndProvider(@Param("email") String email);


    @Query(value = "select * from account a right join account_workspace_role awr on a.id = awr.account_id " +
            "where awr.workspace_id=:idWorkspace and awr.role=1 and (lower(a.email) like lower(concat('%',:searchName,'%'))" +
            "or lower(a.fullname) like lower(concat('%',:searchName,'%')))",nativeQuery = true)
    Page<AccountEntity> findAllBysearchName(@Param("idWorkspace") Integer idWorkspace,
                                            @Param("searchName") String searchName,
                                            Pageable pageable);
    @Query(value = "select * from account a right join account_workspace_role awr on a.id = awr.account_id " +
            "where awr.workspace_id=:idWorkspace and awr.role=1 and (lower(a.email) like lower(concat('%',:searchName,'%'))" +
            "or lower(a.fullname) like lower(concat('%',:searchName,'%')))",nativeQuery = true)
    List<AccountEntity> findAllBysearchNameToList(@Param("idWorkspace") Integer idWorkspace,
                                            @Param("searchName") String searchName);
//    @Query(value = "select * from account a right join account_workspace_role awr on a.id = awr.account_id " +
//            "where awr.workspace_id=:idWorkspace and a.id=:idAccount",nativeQuery = true)
//    Optional<AccountEntity> findByWorkspaceIdAndAccountId(@Param("idWorkspace") Integer idWorkspace,
//                                                          @Param("idAccount") Integer idAccount);
//    @Query("select a from AccountEntity a where a.id=:idAccount and a.authenticationProvider=:provider")
//    Optional<AccountEntity> findByAuthenticationProvider(@Param("idAccount") Integer idAccount,
//                                                         @Param("provider") AuthenticationProvider provider);
}
