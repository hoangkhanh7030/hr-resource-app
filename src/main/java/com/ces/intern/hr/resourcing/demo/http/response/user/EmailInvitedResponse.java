package com.ces.intern.hr.resourcing.demo.http.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EmailInvitedResponse {
    private List<String> emails;
    private String emailSuffix;
}
