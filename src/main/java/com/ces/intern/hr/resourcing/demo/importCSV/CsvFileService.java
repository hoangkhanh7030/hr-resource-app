package com.ces.intern.hr.resourcing.demo.importCSV;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.entity.*;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.report.ResourceReportResponse;
import com.ces.intern.hr.resourcing.demo.http.response.resource.ReportResource;
import com.ces.intern.hr.resourcing.demo.repository.*;
import com.ces.intern.hr.resourcing.demo.utils.CSVFile;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;


@Service
public class CsvFileService {
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final WorkspaceRepository workspaceRepository;
    private final TeamRepository teamRepository;
    private final PositionRepository positionRepository;
    private final ResourceRepository resourceRepository;
    @Autowired
    public CsvFileService(ProjectRepository projectRepository,
                          ModelMapper modelMapper,
                          WorkspaceRepository workspaceRepository,
                          TeamRepository teamRepository,
                          PositionRepository positionRepository,
                          ResourceRepository resourceRepository) {
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
        this.workspaceRepository = workspaceRepository;
        this.teamRepository = teamRepository;
        this.resourceRepository = resourceRepository;
        this.positionRepository = positionRepository;
    }

    public void store(InputStream file, Integer idWorkspace, Integer idAccount) {
        try {

            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            List<ProjectDTO> projectDTOList = ApacheCommonsCsvUtil.parseCsvFile(file);

            for (ProjectDTO projectDTO : projectDTOList) {
                if (!projectRepository.findByNameAndWorkspaceId(projectDTO.getName(),idWorkspace).isPresent()) {
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

    public void storeResource(InputStream file, Integer idWorkspace, Integer idAccount) {
        try {

            WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            List<ResourceRequest> resourceRequests = ApacheCommonsCsvUtil.parseCsvFileResource(file);
            List<PositionEntity> positionEntities = positionRepository.findAllActiveByIdWorkspace(idWorkspace);

            for (ResourceRequest resourceRequest : resourceRequests) {
                ResourceEntity resourceEntity = new ResourceEntity();
                Date current = new Date();
                resourceEntity.setName(resourceRequest.getName());
                resourceEntity.setAvatar(resourceRequest.getAvatar());
                if (teamRepository.findByNameAndidWorkspaceAndFalse(resourceRequest.getTeamName(),idWorkspace).isPresent()){
                    TeamEntity teamEntity = teamRepository.findByNameAndidWorkspaceAndFalse(resourceRequest.getTeamName(),idWorkspace).get();
                    if (positionRepository.findByNameAndIdTeam(resourceRequest.getPositionName(),teamEntity.getId()).isPresent()) {
                        PositionEntity positionEntity = positionRepository.findByNameAndIdTeam(resourceRequest.getPositionName(), teamEntity.getId()).get();
                        resourceEntity.setPositionEntity(positionEntity);
                        resourceEntity.setTeamEntityResource(teamEntity);
                        resourceEntity.setCreatedDate(current);
                        resourceEntity.setCreatedBy(idAccount);
                        resourceEntity.setModifiedBy(idAccount);
                        resourceEntity.setModifiedDate(current);
                        resourceEntity.setVacation(0);
                        resourceEntity.setWorkspaceEntityResource(workspaceEntity);
                        resourceRepository.save(resourceEntity);
                    }else {
                        PositionEntity position = new PositionEntity();
                        position.setName(resourceRequest.getPositionName());
                        position.setTeamEntity(teamEntity);
                        position.setIsArchived(false);
                        positionRepository.save(position);
                        PositionEntity positionEntity = positionRepository.findByNameAndIdTeam(resourceRequest.getPositionName(),teamEntity.getId()).get();
                        resourceEntity.setPositionEntity(positionEntity);
                        resourceEntity.setTeamEntityResource(teamEntity);
                        resourceEntity.setCreatedDate(current);
                        resourceEntity.setCreatedBy(idAccount);
                        resourceEntity.setModifiedBy(idAccount);
                        resourceEntity.setModifiedDate(current);
                        resourceEntity.setVacation(0);
                        resourceEntity.setWorkspaceEntityResource(workspaceEntity);
                        resourceRepository.save(resourceEntity);
                    }
                }else {
                    TeamEntity teamEntity = new TeamEntity();
                    teamEntity.setName(resourceRequest.getTeamName());
                    teamEntity.setIsArchived(false);
                    teamEntity.setCreatedDate(current);
                    teamEntity.setWorkspaceEntityTeam(workspaceEntity);
                    teamRepository.save(teamEntity);
                    TeamEntity team = teamRepository.findByNameAndidWorkspaceAndFalse(resourceRequest.getTeamName(),idWorkspace).get();
                    PositionEntity positionEntity= new PositionEntity();
                    positionEntity.setName(resourceRequest.getPositionName());
                    positionEntity.setTeamEntity(team);
                    positionEntity.setIsArchived(false);
                    positionRepository.save(positionEntity);
                    PositionEntity position = positionRepository.findByNameAndIdTeam(resourceRequest.getPositionName(),team.getId()).get();
                    resourceEntity.setPositionEntity(position);
                    resourceEntity.setTeamEntityResource(team);
                    resourceEntity.setCreatedDate(current);
                    resourceEntity.setCreatedBy(idAccount);
                    resourceEntity.setModifiedBy(idAccount);
                    resourceEntity.setModifiedDate(current);
                    resourceEntity.setVacation(0);
                    resourceEntity.setWorkspaceEntityResource(workspaceEntity);
                    resourceRepository.save(resourceEntity);
                }


            }


        } catch (Exception e) {
            throw new RuntimeException(CSVFile.FAIL_MESSAGE + e.getMessage());
        }
    }



}
