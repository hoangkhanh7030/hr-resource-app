package com.ces.intern.hr.resourcing.demo.http.response.workspace;

import com.ces.intern.hr.resourcing.demo.http.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceResponse extends BaseResponse {
    private String name;
    private String Role;
    private Integer projectListLength;
    private Integer resourceListLength;

}
