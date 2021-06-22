package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.http.exception.BadRequestException;
import com.ces.intern.hr.resourcing.demo.http.exception.LoginException;
import com.ces.intern.hr.resourcing.demo.http.request.AccountLoginRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ErrorResponse;
import com.ces.intern.hr.resourcing.demo.security.RandomStaff;
import com.ces.intern.hr.resourcing.demo.security.config.SecurityContact;
import com.ces.intern.hr.resourcing.demo.security.jwt.JwtTokenProvider;

import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController

public class LoginController {



    private final   JwtTokenProvider tokenProvider;

    private final ModelMapper mapper;
    private final AccountService accountService;
    @Autowired
    public LoginController(JwtTokenProvider tokenProvider, ModelMapper mapper,
                           AccountService accountService) {
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
        this.accountService = accountService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AccountResponse> authenticateUser(@RequestBody AccountLoginRequest accountLoginRequest){
        if(StringUtils.isEmpty(accountLoginRequest.getEmail())||StringUtils.isEmpty(accountLoginRequest.getPassword())){
            throw new BadRequestException(ExceptionMessage.MISSING_REQUIRE_FIELD.getMessage());
        }
        AccountDTO accountDTO = accountService.validateAccount(accountLoginRequest.getEmail(),accountLoginRequest.getPassword());

        final List<String> jwt = tokenProvider.generateToken(accountDTO);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add(SecurityContact.HEADER_STRING,SecurityContact.TOKEN_PREFIX + jwt.get(0));
        responseHeader.add(SecurityContact.HEADER_USERID,jwt.get(1));
        AccountResponse accountResponse = mapper.map(accountDTO,AccountResponse.class);


        return new ResponseEntity<>(accountResponse, responseHeader, HttpStatus.OK);

    }
    @GetMapping(value = "/random")
    @ResponseStatus(value = HttpStatus.OK)
    public RandomStaff randomStaff(){

        return new RandomStaff("hop le");
    }


    @ExceptionHandler({LoginException.class})
    public ResponseEntity<Object> loginError(Exception ex, HttpServletRequest request){
        ErrorResponse response = new ErrorResponse();
        response.setError(ExceptionMessage.USERNAME_PASSWORD_INVALIDATE.getMessage());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURL().toString());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setTimestamp(new Date());
        return new ResponseEntity<>(response,new HttpHeaders(),HttpStatus.NOT_FOUND);
    }


}
