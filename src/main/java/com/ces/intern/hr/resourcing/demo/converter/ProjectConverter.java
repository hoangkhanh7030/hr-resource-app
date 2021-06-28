package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectConverter {
    @Autowired
    private ModelMapper modelMapper;


    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final TimeRepository timeRepository;
    @Autowired
    public ProjectConverter(AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                            TimeRepository timeRepository) {
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.timeRepository = timeRepository;
    }

    public ProjectDTO toDTO(ProjectEntity projectEntity){
        ProjectDTO projectDTO = modelMapper.map(projectEntity,ProjectDTO.class);
        return projectDTO;

    }

}
