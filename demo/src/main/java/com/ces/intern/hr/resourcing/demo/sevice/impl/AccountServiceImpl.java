package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.AccountConverter;
import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccoutRepository accoutRepository;
    @Autowired
    private AccountConverter accountConverter;

    @Override
    public AccountDTO getAccount(int id) {
        if(accoutRepository.findById(id).isPresent()) {
            AccountEntity accountEntity = accoutRepository.findById(id).get();
            AccountDTO accountDTO = accountConverter.toDTO(accountEntity);
            return accountDTO;
        }return null;
    }
}
