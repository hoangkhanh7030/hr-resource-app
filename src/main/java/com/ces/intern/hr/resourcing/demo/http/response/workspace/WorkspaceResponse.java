package com.ces.intern.hr.resourcing.demo.http.response.workspace;

import com.ces.intern.hr.resourcing.demo.http.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceResponse extends BaseResponse {
    private String name;
    private String Role;
    private List<String> emailSuffixes = new ArrayList<>();
    private List<Boolean> workDays = new ArrayList<>();
    private Integer projectListLength;
    private Integer resourceListLength;

}
