package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.request.GoogleRequest;
import com.ces.intern.hr.resourcing.demo.http.response.LoginResponse;
import com.ces.intern.hr.resourcing.demo.http.response.user.AccountResponse;
import com.ces.intern.hr.resourcing.demo.http.response.user.EmailInvitedResponse;

import java.util.List;


public interface AccountService {

    void createdAccount(AccountRequest accountRequest);

    AccountDTO validateAccount(String email, String password);

    void update(AccountRequest accountRequest, Integer modifiedBy);

    EmailInvitedResponse getAllEmailInvited(Integer idWorkspace);

    LoginResponse loginGoogle(GoogleRequest googleRequest);

}
