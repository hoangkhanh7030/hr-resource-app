package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.user.ManageUserResponse;

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
