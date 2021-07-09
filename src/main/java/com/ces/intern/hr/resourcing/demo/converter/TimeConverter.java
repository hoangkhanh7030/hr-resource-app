package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.dto.TimeDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TimeEntity;
import com.ces.intern.hr.resourcing.demo.utils.ObjectMapperUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimeConverter {


    public TimeDTO convertToDto(TimeEntity timeEntity){
        TimeDTO timeDTO = new TimeDTO();
        timeDTO.setId(timeEntity.getId());
        timeDTO.setStartTime(timeEntity.getStartTime());
        timeDTO.setEndTime(timeEntity.getEndTime());
        if (timeEntity.getResourceEntity() != null){
            timeDTO.setProjectDTO(ObjectMapperUtils.map(timeEntity.getProjectEntity(), ProjectDTO.class));
        }
        if(timeEntity.getProjectEntity() != null){
            timeDTO.setResourceDTO(ObjectMapperUtils.map(timeEntity.getResourceEntity(), ResourceDTO.class));
        }
        return timeDTO;
    }

    public TimeEntity convertToEntity(TimeDTO timeDTO){
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setId(timeDTO.getId());
        timeEntity.setStartTime(timeDTO.getStartTime());
        timeEntity.setEndTime(timeDTO.getEndTime());
        if(timeDTO.getProjectDTO() != null){
            timeEntity.setProjectEntity(ObjectMapperUtils.map(timeDTO.getProjectDTO(), ProjectEntity.class));
        }
        if(timeDTO.getResourceDTO() != null){
            timeEntity.setResourceEntity(ObjectMapperUtils.map(timeDTO.getResourceDTO(), ResourceEntity.class));
        }
        return timeEntity;
    }

}
