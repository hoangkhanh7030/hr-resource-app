package com.ces.intern.hr.resourcing.demo.controller;


import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1")
public class AccountController {
    private final AccountService accountService;
    private final AccoutRepository accoutRepository;

    @Autowired
    public AccountController(AccountService accountService,
                             AccoutRepository accoutRepository) {
        this.accountService = accountService;
        this.accoutRepository = accoutRepository;
    }

    @PostMapping(value = "/signup")
    public MessageResponse create(@RequestBody AccountRequest accountRequest) {
        String emailRegex = "^(.+)@(codeenginestudio.com)$";
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (accountRequest.getEmail().matches(emailRegex)) {
            if (accountRequest.getPassword().matches(passwordRegex)) {

                if (accoutRepository.findByEmail(accountRequest.getEmail()).isPresent()) {
                    return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
                } else {
                    accountService.createdAccount(accountRequest);
                }

                if (accoutRepository.findByEmail(accountRequest.getEmail()).isPresent()) {
                    return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
            }
            return new MessageResponse(ResponseMessage.INCRECT_PASSWORD, Status.FAIL.getCode());
        }
        return new MessageResponse(ResponseMessage.INCRECT_EMAIL, Status.FAIL.getCode());
    }

    @PutMapping(value = "/account")
    public MessageResponse updateAccount(@RequestBody AccountRequest accountRequest,
                                         @RequestHeader(value = "AccountId") Integer accountId) {
        String emailRegex = "^(.+)@(codeenginestudio.com)$";
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        if (accountRequest.getEmail().matches(emailRegex)) {
            if (accountRequest.getPassword().matches(passwordRegex)) {
                if (accoutRepository.findByEmail(accountRequest.getEmail()).isPresent()) {
                    return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
                } else {
                    if (accountRequest.getEmail().isEmpty() || accountRequest.getPassword().isEmpty() ||
                            accountRequest.getAvatar().isEmpty() || accountRequest.getFullname().isEmpty()) {
                        return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                    } else {
                        accountService.update(accountRequest, accountId);
                    }
                    if (accoutRepository.findByEmail(accountRequest.getFullname()).isPresent()) {
                        return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                    } else {
                        return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
                    }
                }
            }
            return new MessageResponse(ResponseMessage.INCRECT_PASSWORD, Status.FAIL.getCode());
        }
        return new MessageResponse(ResponseMessage.INCRECT_EMAIL, Status.FAIL.getCode());
    }

    @GetMapping(value = "")
    public AccountResponse getAccount(@RequestHeader(value = "AccountId") Integer accountId) {
        return accountService.getAccount(accountId);
    }

}
