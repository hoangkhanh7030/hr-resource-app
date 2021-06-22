package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Timer;

@Component
public class ProjectConverter {


    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final TimeRepository timeRepository;
    @Autowired
    public ProjectConverter(AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                            TimeRepository timeRepository) {
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.timeRepository = timeRepository;
    }

    public ProjectDTO toDTO(ProjectEntity projectEntity){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(projectEntity.getId());
        projectDTO.setName(projectEntity.getName());
        projectDTO.setColor(projectEntity.getColor());
        projectDTO.setIsActivate(projectEntity.getIsActivate());
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository
                .findByIdWorkspace(projectEntity.getWorkspaceEntityProject().getId()).orElse(null);
        projectDTO.setAccountManager(accountWorkspaceRoleEntity.getAccountEntity().getFullname());
        projectDTO.setProjectManager(timeRepository.findAllByidProject(projectEntity.getId(), Utils.MANAGER.getCode()).getResourceEntity().getName());
        return projectDTO;

    }

}
