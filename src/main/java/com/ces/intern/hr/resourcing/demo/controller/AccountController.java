package com.ces.intern.hr.resourcing.demo.controller;


import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;

import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.AccountService;
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
    public String create(@RequestBody AccountRequest accountRequest) {
        return accountService.createdAccount(accountRequest);

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
