package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.response.ProjectResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.repository.TimeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class WorkspaceConverter {

    private final ModelMapper modelMapper;
    private final TimeRepository timeRepository;
    @Autowired
    public WorkspaceConverter(ModelMapper modelMapper, TimeRepository timeRepository) {
        this.modelMapper = modelMapper;
        this.timeRepository = timeRepository;
    }

    public WorkspaceDTO toDTO(WorkspaceEntity workspaceEntity){
        WorkspaceDTO workspaceDTO = modelMapper.map(workspaceEntity,WorkspaceDTO.class);
        workspaceDTO.setProjectList(projectResponseList(workspaceEntity));
        workspaceDTO.setResourceList(resourceResponseList(workspaceEntity));

        return workspaceDTO;
    }
    public WorkspaceEntity toEntity(WorkspaceDTO workspaceDTO){
        WorkspaceEntity workspaceEntity = modelMapper.map(workspaceDTO,WorkspaceEntity.class);
        return workspaceEntity;
    }


    public List<ProjectDTO> projectDTOList(WorkspaceEntity workspaceEntity){
        List<ProjectDTO> list = new ArrayList<>();
        int size =workspaceEntity.getProjectEntities().size();
        if(size>0){
        for (int i=0;i<size;i++){
                ProjectDTO projectDTO = modelMapper.map(
                  workspaceEntity.getProjectEntities().get(i),ProjectDTO.class
                );
                list.add(projectDTO);
        }
        }
        return list;
    }
    public List<ProjectResponse> projectResponseList(WorkspaceEntity workspaceEntity){
        List<ProjectResponse> list = new ArrayList<>();
        int size = workspaceEntity.getProjectEntities().size();
        if(size>0){
            for (int i=0;i<size;i++){
                ProjectResponse projectResponse = modelMapper.map(workspaceEntity.getProjectEntities().get(i),ProjectResponse.class);
                projectResponse.setActivate(workspaceEntity.getProjectEntities().get(i).getIsActivate());
                list.add(projectResponse);
            }
        }
        return list;
    }
    public List<ResourceDTO> resourceDTOList(WorkspaceEntity workspaceEntity){
        List<ResourceDTO> list = new ArrayList<>();
        int size = workspaceEntity.getResourceEntities().size();
        if(size > 0){
        for (int i=0;i< size;i++){
                ResourceDTO resourceDTO = modelMapper.map(
                        workspaceEntity.getResourceEntities().get(i),ResourceDTO.class
                );
                list.add(resourceDTO);
        }
        }
        return list;
    }
    public List<ResourceResponse> resourceResponseList(WorkspaceEntity workspaceEntity){
        List<ResourceResponse> list = new ArrayList<>();
        int size = workspaceEntity.getResourceEntities().size();
        if (size>0){
            for (int i=0;i<size;i++){
                ResourceResponse resourceResponse = modelMapper.map(
                        workspaceEntity.getResourceEntities().get(i),ResourceResponse.class
                );
                resourceResponse.setTeam(workspaceEntity.getResourceEntities().get(i).getTeamEntity().getName());
                resourceResponse.setPosition(workspaceEntity.getResourceEntities().get(i).getPositionEntity().getName());
                list.add(resourceResponse);
            }
        }
        return list;
    }

}
