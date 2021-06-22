package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;

import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getProjects(Integer idAccount,Integer idWorkspace);


}
