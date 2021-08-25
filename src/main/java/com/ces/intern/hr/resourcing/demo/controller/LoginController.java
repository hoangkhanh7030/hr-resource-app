package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.BadRequestException;
import com.ces.intern.hr.resourcing.demo.http.exception.LoginException;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.AccountLoginRequest;
import com.ces.intern.hr.resourcing.demo.http.request.GoogleRequest;
import com.ces.intern.hr.resourcing.demo.http.response.error.ErrorResponse;
import com.ces.intern.hr.resourcing.demo.http.response.LoginResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.AuthenticationProvider;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(value = "/api/v1")
public class LoginController {


    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;
    private final AccoutRepository accoutRepository;
    private final ModelMapper modelMapper;



    @Autowired
    public LoginController(JwtTokenProvider tokenProvider,
                           AccountService accountService,
                           AccoutRepository accoutRepository,
                           ModelMapper modelMapper
                          ) {
        this.tokenProvider = tokenProvider;
        this.accountService = accountService;
        this.accoutRepository=accoutRepository;
        this.modelMapper=modelMapper;

    }

    @PostMapping(value = "/login")
    public LoginResponse authenticateUser(@RequestBody AccountLoginRequest accountLoginRequest) {

            if (StringUtils.isEmpty(accountLoginRequest.getEmail()) || StringUtils.isEmpty(accountLoginRequest.getPassword())) {
                throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
            }
            AccountDTO accountDTO = accountService.validateAccount(accountLoginRequest.getEmail(), accountLoginRequest.getPassword());

            String jwt = tokenProvider.generateToken(accountDTO);

            return new LoginResponse(jwt, accountDTO, Status.SUCCESS.getCode());

    }

    @PostMapping(value = "/auth/google")
    public LoginResponse authGoogle(@RequestBody GoogleRequest googleRequest){
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
            return new LoginResponse(jwt,accountDTO,Status.SUCCESS.getCode());
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





    @ExceptionHandler({LoginException.class})
    public ResponseEntity<Object> loginError(Exception ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setError(ExceptionMessage.USERNAME_PASSWORD_INVALIDATE.getMessage());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURL().toString());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


}
