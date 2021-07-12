package com.ces.intern.hr.resourcing.demo.dto;



import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO extends BaseDTO{
    private String email;
    private String password;
    private String fullname;
    private String avatar;
    private List<WorkspaceDTO> workspaceDTOS;

}
