package com.ces.intern.hr.resourcing.demo.http.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageResponse {
    private List<ManageUserResponse> manageUsers;
    private Integer numberSize;
    private Integer adminId;
}
