package com.ces.intern.hr.resourcing.demo.http.request;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceRequest {
    private String name;
    private String avatar;
    private String workspaceName;
    private List<ProjectDTO> listProject;
}
