package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AccoutRepository extends JpaRepository<AccountEntity,Integer> {
    AccountEntity getAccountEntitiesByEmail(String email);

    Optional<AccountEntity> findByEmail(String email);
    Integer countByEmail(String email);
    Integer countByFullname(String name);
    Optional<AccountEntity> findByFullname(String name);
}
