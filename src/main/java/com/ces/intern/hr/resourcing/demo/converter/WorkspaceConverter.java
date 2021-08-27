package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkspaceConverter {
    @Autowired
    private ModelMapper modelMapper;

    public WorkspaceEntity convertToEntity(WorkspaceDTO workspaceDTO){
//        WorkspaceEntity workspaceEntity = new WorkspaceEntity();
//        workspaceEntity.setName(workspaceDTO.getName());
//        workspaceEntity.setEmailSuffix(workspaceDTO.getEmailSuffix());
//        if (workspaceDTO.getResources() != null){
//            workspaceEntity.setResourceEntities(ObjectMapperUtils.mapAll(workspaceDTO.getResources(), ResourceEntity.class));
//        }
//        if (workspaceDTO.getProjects() != null){
//            workspaceEntity.setProjectEntities(ObjectMapperUtils.mapAll(workspaceDTO.getProjects(), ProjectEntity.class));
//        }
        WorkspaceEntity workspaceEntity = modelMapper.map(workspaceDTO, WorkspaceEntity.class);
        StringBuilder workDays = new StringBuilder();
        for (Boolean day : workspaceDTO.getWorkDays()){
            if (day){
                workDays.append("true");
            }
            else {
                workDays.append("false");
            }
            if (workspaceDTO.getWorkDays().indexOf(day) != workspaceDTO.getWorkDays().size()){
                workDays.append(",");
            }
        }
        workspaceEntity.setWorkDays(workDays.toString());
        return workspaceEntity;
    }

    public WorkspaceDTO convertToDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity, WorkspaceDTO.class);
        List<Boolean> workDays = new ArrayList<>();
        String[] array = workspaceEntity.getWorkDays().split(",");
        for (String string : array){
            workDays.add(Boolean.parseBoolean(string));
        }
        workspaceDTO.setWorkDays(workDays);
        return workspaceDTO;
    }
}
