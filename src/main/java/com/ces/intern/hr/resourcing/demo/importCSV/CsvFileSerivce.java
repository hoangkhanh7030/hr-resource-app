package com.ces.intern.hr.resourcing.demo.importCSV;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.ProjectEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.ProjectRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
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

    @Autowired
    public CsvFileSerivce(ProjectRepository projectRepository,
                          ModelMapper modelMapper,
                          WorkspaceRepository workspaceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.workspaceRepository = workspaceRepository;
    }

    public void store(InputStream file,Integer idWorkspace,Integer idAccount) {
        try {

            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            List<ProjectDTO> projectDTOList = ApacheCommonsCsvUtil.parseCsvFile(file);
            List<ProjectEntity> projectEntityList = new ArrayList<>();
            for (ProjectDTO projectDTO : projectDTOList) {
                ProjectEntity projectEntity = modelMapper.map(projectDTO, ProjectEntity.class);
                projectEntity.setWorkspaceEntityProject(workspaceEntity);
                projectEntity.setIsActivate(projectDTO.getIsActivate());
                projectEntity.setCreatedDate(new Date());
                projectEntity.setCreatedBy(idAccount);
                projectEntityList.add(projectEntity);

            }

            projectRepository.saveAll(projectEntityList);
        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }
    }
}
