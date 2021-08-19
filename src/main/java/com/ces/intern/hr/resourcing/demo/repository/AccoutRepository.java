package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccoutRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity getAccountEntitiesByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);

    @Query(value = "select * from account a where a.email=:email and a.auth_provider='NOTHING'", nativeQuery = true)
    Optional<AccountEntity> findByEmailAndProvider(@Param("email") String email);
}
