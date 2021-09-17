package com.ces.intern.hr.resourcing.demo.sevice.impl;


import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.request.GoogleRequest;
import com.ces.intern.hr.resourcing.demo.http.response.LoginResponse;
import com.ces.intern.hr.resourcing.demo.http.response.user.EmailInvitedResponse;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.http.exception.LoginException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;

import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccoutRepository accoutRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AccountServiceImpl(AccoutRepository accoutRepository,
                              PasswordEncoder passwordEncoder,
                              ModelMapper modelMapper,
                              WorkspaceRepository workspaceRepository,
                              JwtTokenProvider tokenProvider) {
        this.accoutRepository = accoutRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.workspaceRepository=workspaceRepository;
        this.tokenProvider=tokenProvider;
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
    public EmailInvitedResponse getAllEmailInvited(Integer idWorkspace) {
        WorkspaceEntity workspaceEntity=workspaceRepository.findByIdWorkspace(idWorkspace).orElse(null);
        List<AccountEntity> accountEntities=accoutRepository.findAllByWorkspaceId(idWorkspace);
        List<String> emails=new ArrayList<>();
        for (AccountEntity accountEntity:accountEntities){
            if (accountEntity!=null){
                String email =accountEntity.getEmail();
                emails.add(email);
            }
        }
        EmailInvitedResponse emailInvitedResponse= new EmailInvitedResponse();
        emailInvitedResponse.setEmails(emails);
        assert workspaceEntity != null;
        String[] arrayEmailSuffixes = workspaceEntity.getEmailSuffix().split(",");
        if (!workspaceEntity.getEmailSuffix().isEmpty()){
            List<String> emailSuffixes = new ArrayList<>(Arrays.asList(arrayEmailSuffixes));
            emailInvitedResponse.setEmailSuffixes(emailSuffixes);
        }
        else {
            emailInvitedResponse.setEmailSuffixes(new ArrayList<>());
        }
       return emailInvitedResponse;
    }

    @Override
    public LoginResponse loginGoogle(GoogleRequest googleRequest) {
        if (accoutRepository.findByEmailAndProvider(googleRequest.getEmail()).isPresent()){
            AccountEntity accountEntity=accoutRepository.findByEmail(googleRequest.getEmail()).get();
            accountEntity.setEmail(googleRequest.getEmail());
            accountEntity.setFullname(googleRequest.getName());
            accountEntity.setAvatar(googleRequest.getImageUrl());
            accountEntity.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
            accountEntity.setCreatedDate(new Date());
            accoutRepository.save(accountEntity);
            AccountDTO  accountDTO =modelMapper.map(accountEntity,AccountDTO.class);
            String jwt = tokenProvider.generateToken(accountDTO);
            return new LoginResponse(jwt,accountDTO, Status.SUCCESS.getCode());
        }
        else if (accoutRepository.findByEmail(googleRequest.getEmail()).isPresent()){
            AccountEntity accountEntity = accoutRepository.findByEmail(googleRequest.getEmail())
                    .orElseThrow(()-> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            AccountDTO accountDTO = modelMapper.map(accountEntity,AccountDTO.class);
            String jwt =tokenProvider.generateToken(accountDTO);
            return new LoginResponse(jwt,accountDTO,Status.SUCCESS.getCode());

        }else {
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setEmail(googleRequest.getEmail());
            accountEntity.setFullname(googleRequest.getName());
            accountEntity.setAvatar(googleRequest.getImageUrl());
            accountEntity.setAuthenticationProvider(AuthenticationProvider.GOOGLE);
            accountEntity.setCreatedDate(new Date());
            accoutRepository.save(accountEntity);
            AccountEntity account = accoutRepository.findByEmail(googleRequest.getEmail())
                    .orElseThrow(()-> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            AccountDTO  accountDTO =modelMapper.map(account,AccountDTO.class);
            String jwt = tokenProvider.generateToken(accountDTO);
            return new LoginResponse(jwt,accountDTO,Status.SUCCESS.getCode());
        }
    }


}
