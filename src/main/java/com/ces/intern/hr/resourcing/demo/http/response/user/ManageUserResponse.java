package com.ces.intern.hr.resourcing.demo.http.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageUserResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String status;
    private String role;
    private Date createdDate;

}
