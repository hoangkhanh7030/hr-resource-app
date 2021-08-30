package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
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
            if (workspaceDTO.getWorkDays().indexOf(day) != workspaceDTO.getWorkDays().size() - 1){
                workDays.append(",");
            }
        }
        StringBuilder emailSuffixes = new StringBuilder();
        if (workspaceDTO.getEmailSuffixes().size() != 0) {
            for (String suffix : workspaceDTO.getEmailSuffixes()) {
                emailSuffixes.append(suffix);
                if (workspaceDTO.getEmailSuffixes().indexOf(suffix) != workspaceDTO.getEmailSuffixes().size() - 1) {
                    emailSuffixes.append(",");
                }
            }
            workspaceEntity.setEmailSuffix(emailSuffixes.toString());
        }
        else {
            workspaceEntity.setEmailSuffix("");
        }
        workspaceEntity.setWorkDays(workDays.toString());
        return workspaceEntity;
    }

    public WorkspaceDTO convertToDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity, WorkspaceDTO.class);
        List<Boolean> workDays = new ArrayList<>();
        String[] arrayWorkDays = workspaceEntity.getWorkDays().split(",");
        String[] arrayEmailSuffixes = workspaceEntity.getEmailSuffix().split(",");
        for (String string : arrayWorkDays){
            workDays.add(Boolean.parseBoolean(string));
        }
        if (!workspaceEntity.getEmailSuffix().equals("")){
            List<String> emailSuffixes = new ArrayList<>(Arrays.asList(arrayEmailSuffixes));
            workspaceDTO.setEmailSuffixes(emailSuffixes);
        }
        else {
            workspaceDTO.setEmailSuffixes(new ArrayList<>());
        }
        workspaceDTO.setWorkDays(workDays);
        return workspaceDTO;
    }
}
