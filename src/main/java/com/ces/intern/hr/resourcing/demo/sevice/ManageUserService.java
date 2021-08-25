package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.http.request.AccountRequest;
import com.ces.intern.hr.resourcing.demo.http.request.InviteRequest;
import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.ManageUserResponse;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface ManageUserService {
    List<ManageUserResponse> getAll(Integer idWorkspace,Integer page,
                                    Integer size,String searchName,String sortName,
                                    String type);
    void delete(Integer idAccount,Integer idWorkspace);
    void sendEmail(ReInviteRequest reInviteRequest) throws MessagingException, IOException;
    void isActive(Integer idAccount);
}
