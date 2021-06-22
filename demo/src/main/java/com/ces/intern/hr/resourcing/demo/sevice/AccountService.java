package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.response.AccountResponse;


public interface AccountService {

    String createdAccount(AccountRequest accountRequest);
    AccountDTO validateAccount(String email,String password);
    AccountResponse update(AccountRequest accountRequest,Integer modifiedBy);
    AccountResponse getAccount(Integer idAccount);

}
