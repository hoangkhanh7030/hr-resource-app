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
        String emailRegex = "^(.+)@(codeengine.com)$";
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
    public AccountResponse updateAccount(@RequestBody AccountRequest accountRequest,
                                         @RequestHeader(value = "AccountId") Integer accountId) {
        return accountService.update(accountRequest, accountId);
    }

    @GetMapping(value = "")
    public AccountResponse getAccount(@RequestHeader(value = "AccountId") Integer accountId) {
        return accountService.getAccount(accountId);
    }

}
