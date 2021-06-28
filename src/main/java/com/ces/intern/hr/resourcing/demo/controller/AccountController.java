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
@RequestMapping(value = "/account")
public class AccountController {
    private final AccountService accountService;
    private final AccoutRepository accoutRepository;

    @Autowired
    public AccountController(AccountService accountService,
                             AccoutRepository accoutRepository) {
        this.accountService = accountService;
        this.accoutRepository = accoutRepository;
    }

    @PostMapping(value = "/create")
    public MessageResponse create(@RequestBody AccountRequest accountRequest) {
        accountRequest.setEmail(accountRequest.getEmail().toLowerCase());
        if (accoutRepository.countByEmail(accountRequest.getEmail()) == 1) {
            return new MessageResponse(accountRequest.getEmail() + " " + ResponseMessage.ALREADY_EXIST,Status.FAIL.getCode());
        }
            accountService.createdAccount(accountRequest);
            if (accoutRepository.findByEmail(accountRequest.getEmail()).isPresent()){
                return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());
            }return new MessageResponse(ResponseMessage.CREATE_FAIL,Status.FAIL.getCode());


    }

    @PutMapping(value = "/update")
    public AccountResponse updateAccount(@RequestBody AccountRequest accountRequest,
                                         @RequestHeader(value = "AccountId") Integer accountId) {
        return accountService.update(accountRequest, accountId);
    }

    @GetMapping(value = "")
    public AccountResponse getAccount(@RequestHeader(value = "AccountId") Integer accountId) {
        return accountService.getAccount(accountId);
    }

}
