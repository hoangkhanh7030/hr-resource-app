package com.ces.intern.hr.resourcing.demo.repository;

import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccoutRepository extends JpaRepository<AccountEntity,Integer> {
    AccountEntity getAccountEntitiesByEmail(String email);
}
