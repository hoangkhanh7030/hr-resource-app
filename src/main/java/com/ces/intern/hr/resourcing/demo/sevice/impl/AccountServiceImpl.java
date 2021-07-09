package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.http.exception.LoginException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccoutRepository accoutRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public AccountServiceImpl(AccoutRepository accoutRepository,
                              PasswordEncoder passwordEncoder,
                              ModelMapper modelMapper) {
        this.accoutRepository = accoutRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createdAccount(AccountRequest accountRequest) {

        String encodePassword = passwordEncoder.encode(accountRequest.getPassword());
        AccountEntity accountEntity = modelMapper.map(accountRequest, AccountEntity.class);
        accountEntity.setPassword(encodePassword);
        accountEntity.setAuthenticationProvider(AuthenticationProvider.LOCAL);
        Date date = new Date();
        accountEntity.setCreatedDate(date);
        accountEntity.setModifiedDate(date);
        accountEntity = accoutRepository.save(accountEntity);
        accountEntity.setCreatedBy(accountEntity.getId());
        accountEntity.setModifiedBy(accountEntity.getId());
        accoutRepository.save(accountEntity);


    }

    @Override
    public AccountDTO validateAccount(String email, String password) {
        AccountEntity accountEntity = accoutRepository.findByEmail(email)
                .orElseThrow(() -> new LoginException(ExceptionMessage.USERNAME_PASSWORD_INVALIDATE.getMessage()));
        boolean validate = passwordEncoder.matches(password, accountEntity.getPassword());
        if (!validate) throw new LoginException(ExceptionMessage.USERNAME_PASSWORD_INVALIDATE.getMessage());
        return modelMapper.map(accountEntity, AccountDTO.class);
    }

    @Override
    public void update(AccountRequest accountRequest, Integer modifiedBy) {
        AccountDTO accountDTO = modelMapper.map(accountRequest, AccountDTO.class);
        accountDTO.setId(modifiedBy);
        accountDTO.setModifiedDate(new Date());
        accountDTO.setModifiedBy(modifiedBy);

        AccountEntity accountEntity = accoutRepository.findById(modifiedBy)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        accountDTO.setCreatedBy(accountEntity.getCreatedBy());
        accountDTO.setCreatedDate(accountEntity.getCreatedDate());
        accountDTO.setEmail(accountEntity.getEmail());
        accountDTO.setPassword(accountEntity.getPassword());
        accountEntity = modelMapper.map(accountDTO, AccountEntity.class);
        accoutRepository.save(accountEntity);

    }

    @Override
    public AccountResponse getAccount(Integer idAccount) {

        AccountEntity accountEntity = accoutRepository.findById(idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        return modelMapper.map(accountEntity, AccountResponse.class);
    }


}
