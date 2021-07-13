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
    private String fullName;
    private String avatar;

    public boolean validate() {
        if (email == null || email.isEmpty() || password == null || password.isEmpty() ||
                avatar == null || avatar.isEmpty() || fullName == null || fullName.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}
