package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import com.ces.intern.hr.resourcing.demo.utils.ObjectMapperUtils;
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

    public ProjectDTO convertToDto(ProjectEntity projectEntity){
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(projectEntity.getId());
        projectDTO.setCreatedBy(projectEntity.getCreatedBy());
        projectDTO.setModifiedBy(projectEntity.getModifiedBy());
        projectDTO.setCreatedDate(projectEntity.getCreatedDate());
        projectDTO.setModifiedDate(projectEntity.getModifiedDate());
        projectDTO.setColor(projectEntity.getColor());
        projectDTO.setIsActivate(projectEntity.getIsActivate());
        projectDTO.setName(projectEntity.getName());
//        projectDTO.setListTime(timeConverter.getDtoList(projectEntity.getListTime()));
//        projectDTO.setWorkSpaceDTO(workSpaceConverter.convertToDto(projectEntity.getWorkSpaceEntity()));
        if(projectEntity.getTimeEntities() != null){
            projectDTO.setTimeDTOList(ObjectMapperUtils.mapAll(projectEntity.getTimeEntities(), TimeDTO.class));
        }
        return projectDTO;
    }

    public ProjectEntity convertToEntity(ProjectDTO projectDTO){
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(projectDTO.getId());
        projectEntity.setCreatedBy(projectDTO.getCreatedBy());
        projectEntity.setModifiedBy(projectDTO.getModifiedBy());
        projectEntity.setCreatedDate(projectDTO.getCreatedDate());
        projectEntity.setModifiedDate(projectDTO.getModifiedDate());
        projectEntity.setColor(projectDTO.getColor());
        projectEntity.setIsActivate(projectDTO.getIsActivate());
        projectEntity.setName(projectDTO.getName());
//        projectDTO.setListTime(timeConverter.getDtoList(projectEntity.getListTime()));
//        projectDTO.setWorkSpaceDTO(workSpaceConverter.convertToDto(projectEntity.getWorkSpaceEntity()));
        if(projectDTO.getTimeDTOList() != null){
            projectEntity.setTimeEntities(ObjectMapperUtils.mapAll(projectDTO.getTimeDTOList(), TimeEntity.class));
        }
        return projectEntity;
    }

}
