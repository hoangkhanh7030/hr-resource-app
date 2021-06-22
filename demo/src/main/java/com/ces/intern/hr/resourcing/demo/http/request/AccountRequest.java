package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String email;
    private String password;
    private String fullname;
    private String avatar;
}
