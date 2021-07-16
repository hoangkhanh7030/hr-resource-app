package com.ces.intern.hr.resourcing.demo.importCSV;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.utils.CSVFile;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class CsvFileSerivce {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public CsvFileSerivce(ProjectRepository projectRepository,
                          ModelMapper modelMapper,
                          WorkspaceRepository workspaceRepository,
                          TeamRepository teamRepository,
                          PositionRepository positionRepository,
                          ResourceRepository resourceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.workspaceRepository = workspaceRepository;
        this.teamRepository= teamRepository;
        this.resourceRepository=resourceRepository;
        this.positionRepository=positionRepository;
    }

    public void store(InputStream file,Integer idWorkspace,Integer idAccount) {
        try {

            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            List<ProjectDTO> projectDTOList = ApacheCommonsCsvUtil.parseCsvFile(file);

            for (ProjectDTO projectDTO : projectDTOList) {
                if (projectRepository.findByName(projectDTO.getName()).isPresent()){
                    continue;
                }else {
                    ProjectEntity projectEntity = modelMapper.map(projectDTO, ProjectEntity.class);
                    projectEntity.setWorkspaceEntityProject(workspaceEntity);
                    projectEntity.setIsActivate(projectDTO.getIsActivate());
                    projectEntity.setCreatedDate(new Date());
                    projectEntity.setCreatedBy(idAccount);
                    projectRepository.save(projectEntity);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }
    }

    public void storeResource(InputStream file,Integer idWorkspace,Integer idAccount) {
        try {

            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            List<ResourceRequest> resourceRequests = ApacheCommonsCsvUtil.parseCsvFileResource(file);

            for (ResourceRequest resourceRequest : resourceRequests) {
                ResourceEntity resourceEntity = new ResourceEntity();
                resourceEntity.setName(resourceRequest.getName());
                resourceEntity.setAvatar(resourceRequest.getAvatar());
                resourceEntity.setWorkspaceEntityResource(workspaceEntity);
                resourceEntity.setTeamEntity(teamRepository.findById(resourceRequest.getTeamId()).orElse(null));
                resourceEntity.setPositionEntity(positionRepository.findById(resourceRequest.getPositionId()).orElse(null));
                resourceEntity.setCreatedDate(new Date());
                resourceEntity.setCreatedBy(idAccount);
                resourceRepository.save(resourceEntity);
            }


        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }
    }
}
