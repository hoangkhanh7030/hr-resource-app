package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.converter.AccountConverter;
import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.http.exception.AlreadyExistException;
import com.ces.intern.hr.resourcing.demo.http.exception.LoginException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Date;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccoutRepository accoutRepository;
    @Autowired
    private AccountConverter accountConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public String createdAccount(AccountRequest accountRequest) throws AlreadyExistException {
        accountRequest.setEmail(accountRequest.getEmail().toLowerCase());
        if (accoutRepository.countByEmail(accountRequest.getEmail()) == 1) {
            throw new AlreadyExistException(ExceptionMessage.EMAIL_ALREADY_EXIST.getMessage());
        } else {
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
            return ResponseMessage.CREATE_SUCCESS;
        }


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
    public AccountResponse update(AccountRequest accountRequest, Integer modifiedBy) {
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

        return modelMapper.map(accountDTO, AccountResponse.class);
    }

    @Override
    public AccountResponse getAccount(Integer idAccount) {

        AccountEntity accountEntity = accoutRepository.findById(idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        return modelMapper.map(accountEntity, AccountResponse.class);
    }


}
