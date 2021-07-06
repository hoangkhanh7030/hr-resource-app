package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;


public interface AccountService {

    void createdAccount(AccountRequest accountRequest);

    AccountDTO validateAccount(String email, String password);

    void update(AccountRequest accountRequest, Integer modifiedBy);

    AccountResponse getAccount(Integer idAccount);

}
