package com.ces.intern.hr.resourcing.demo.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponse extends BaseResponse{
    private String email;
    private String password;
    private String fullname;
    private String avatar;

}
