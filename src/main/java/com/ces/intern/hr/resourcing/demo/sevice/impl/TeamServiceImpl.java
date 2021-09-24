package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.PositionDTO;
import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.PositionEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.entity.WorkspaceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.PositionRequest;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.PositionResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.TeamResponse;
import com.ces.intern.hr.resourcing.demo.repository.PositionRepository;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ResourceRepository resourceRepository;
    private final WorkspaceRepository workspaceRepository;
    private final PositionRepository positionRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository,
                           ModelMapper modelMapper,
                           ResourceRepository resourceRepository,
                           WorkspaceRepository workspaceRepository,
                           PositionRepository positionRepository
    ) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.resourceRepository = resourceRepository;
        this.workspaceRepository = workspaceRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public List<TeamDTO> getAll(Integer idWorkspace) {
        List<TeamEntity> teamEntityList = teamRepository.findAllActiveByWorkspaceId(idWorkspace);
        return teamEntityList.stream().map(s -> modelMapper.map(s, TeamDTO.class)).collect(Collectors.toList());
    }


    @Override
    public MessageResponse deleteTeam(Integer idTeam) {
        TeamEntity teamEntity = teamRepository.findById(idTeam)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        teamRepository.delete(teamEntity);
        if (teamRepository.findById(idTeam).isPresent()) {
            return new MessageResponse(ResponseMessage.DELETE_TEAM_FAIL, Status.FAIL.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_TEAM_SUCCESS, Status.SUCCESS.getCode());
    }

    @Override
    public MessageResponse renameTeam(Integer idWorkspace, Integer idTeam, TeamDTO teamDTO) {

        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) {
            return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
        } else {
            TeamEntity teamEntity = teamRepository.findByidWorkspaceAndIdTeam(idWorkspace, idTeam)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
            teamEntity.setName(teamDTO.getName());
            teamRepository.save(teamEntity);
            if (teamRepository.findByNameAndidWorkspace(teamDTO.getName(), idWorkspace).isPresent()) {
                return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
            }
            return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
        }

    }


    @Override
    public List<TeamResponse> getTeams(Integer idWorkspace) {
        List<TeamEntity> teamEntities = teamRepository.findAllByidWorkspace(idWorkspace);
        List<TeamResponse> teamResponses = new ArrayList<>();
        for (TeamEntity teamEntity : teamEntities) {
            TeamResponse teamResponse = new TeamResponse();
            List<PositionEntity> positionEntities = positionRepository.findAllByidWorkspaceAndidTeam(idWorkspace, teamEntity.getId());
            List<PositionResponse> positionResponses = positionEntities.stream().map(
                    positionEntity -> modelMapper.map(positionEntity, PositionResponse.class)
            ).collect(Collectors.toList());
            teamResponse.setId(teamEntity.getId());
            teamResponse.setName(teamEntity.getName());
            teamResponse.setPositions(positionResponses);
            teamResponses.add(teamResponse);
        }
        return teamResponses;
    }

    private void updateTeam(TeamRequest teamRequest, Integer idWorkspace) {
        TeamEntity teamEntity = teamRepository.findById(teamRequest.getId())
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        teamEntity.setName(teamRequest.getName());
        teamRepository.save(teamEntity);
        for (PositionRequest positionRequest : teamRequest.getPositions()) {
            if (positionRequest.getId() == null) {
                if (positionRepository.findByidTeamAndTrue(teamEntity.getId(), positionRequest.getName()).isPresent()) {
                    PositionEntity positionEntity = positionRepository.findByidTeamAndTrue(teamEntity.getId(), positionRequest.getName()).get();
                    positionEntity.setIsArchived(false);
                    positionRepository.save(positionEntity);
                } else {
                    PositionEntity positionEntity = new PositionEntity();
                    positionEntity.setName(positionRequest.getName());
                    positionEntity.setTeamEntity(teamRepository.findById(teamRequest.getId()).orElse(null));
                    positionRepository.save(positionEntity);
                }
            } else {
                PositionEntity positionEntity = positionRepository.findById(positionRequest.getId()).orElse(null);
                positionEntity.setName(positionRequest.getName());
                positionEntity.setTeamEntity(teamRepository.findById(teamRequest.getId()).orElse(null));
                positionRepository.save(positionEntity);
            }

        }
    }

    @Override
    public MessageResponse created(List<TeamRequest> teamRequests, Integer idWorkspace) {
        for (TeamRequest teamRequest : teamRequests) {
            if (teamRequest.getName().isEmpty()) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                for (PositionRequest positionRequest : teamRequest.getPositions()) {
                    if (positionRequest.getName().isEmpty()) {
                        return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                    }
                }
            }
        }
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).orElse(null);
        for (TeamRequest teamRequest : teamRequests) {
            if (!teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace).isPresent()) {
                TeamEntity teamEntity = new TeamEntity();
                teamEntity.setName(teamRequest.getName());
                teamEntity.setCreatedDate(new Date());
                teamEntity.setWorkspaceEntityTeam(workspaceEntity);
                teamRepository.save(teamEntity);
                for (PositionRequest positionRequest : teamRequest.getPositions()) {
                    if (!positionRepository.findByNameAndTeamEntity_Id(positionRequest.getName(),
                            teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace)
                                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())).getId()).isPresent()) {
                        PositionEntity positionEntity = new PositionEntity();
                        positionEntity.setName(positionRequest.getName());
                        positionEntity.setTeamEntity(teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace)
                                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
                        positionRepository.save(positionEntity);
                    }
                }
            }
        }
        return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());


    }

    @Override
    public MessageResponse update(List<TeamRequest> teamRequests, Integer idWorkspace) {
        List<TeamEntity> teamEntities = teamRepository.findAllByidWorkspace(idWorkspace);
        for (TeamRequest teamRequest : teamRequests) {
            List<PositionEntity> positionEntities = positionRepository.findAllByTeamEntity_Id(teamRequest.getId());
            if (teamRequest.getId() == null) {
                createWithIdTeam(teamRequest, idWorkspace);
            } else {
                updateTeam(teamRequest, idWorkspace);
            }
            deletePosition(teamRequest.getPositions(), positionEntities);
        }
        deleteTeam(teamRequests, teamEntities);
        if (listEquals(toDTO(teamRequests), EntitytoDTO(teamEntities))) {
            return new MessageResponse(ResponseMessage.SETTING_SUCCESS, Status.SUCCESS.getCode());
        } else {
            return new MessageResponse(ResponseMessage.SETTING_FAIL, Status.FAIL.getCode());
        }

    }


    private void createWithIdTeam(TeamRequest teamRequest, Integer idWorkspace) {
        WorkspaceEntity workspaceEntity = workspaceRepository.findById(idWorkspace).orElse(null);
        if (teamRepository.findByNameAndidWorkspaceAndTrue(idWorkspace, teamRequest.getName()).isPresent()) {
            TeamEntity teamEntity = teamRepository.findByNameAndidWorkspaceAndTrue(idWorkspace, teamRequest.getName()).get();
            teamEntity.setIsArchived(false);
            teamRepository.save(teamEntity);
            for (PositionRequest positionRequest : teamRequest.getPositions()) {
                if (positionRepository.findByidTeamAndTrue(teamEntity.getId(), positionRequest.getName()).isPresent()) {
                    PositionEntity positionEntity = positionRepository.findByidTeamAndTrue(teamEntity.getId(), positionRequest.getName()).get();
                    positionEntity.setIsArchived(false);
                    positionRepository.save(positionEntity);
                } else {
                    PositionEntity positionEntity = new PositionEntity();
                    positionEntity.setName(positionRequest.getName());
                    positionEntity.setTeamEntity(teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace)
                            .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
                    positionRepository.save(positionEntity);
                }
            }
        } else {
            TeamEntity teamEntity = new TeamEntity();
            teamEntity.setName(teamRequest.getName());
            teamEntity.setCreatedDate(new Date());
            teamEntity.setWorkspaceEntityTeam(workspaceEntity);
            teamRepository.save(teamEntity);
            for (PositionRequest positionRequest : teamRequest.getPositions()) {
                if (!positionRepository.findByNameAndTeamEntity_Id(positionRequest.getName(),
                        teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace)
                                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())).getId()).isPresent()) {
                    PositionEntity positionEntity = new PositionEntity();
                    positionEntity.setName(positionRequest.getName());
                    positionEntity.setTeamEntity(teamRepository.findByNameAndidWorkspace(teamRequest.getName(), idWorkspace)
                            .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage())));
                    positionRepository.save(positionEntity);
                }
            }
        }

    }


    private void deleteTeam(List<TeamRequest> teamRequests, List<TeamEntity> teamEntities) {
        for (TeamEntity teamEntity : teamEntities) {
            if (teamRequests.stream().filter(s -> teamEntity.getName().equals(s.getName())).findAny().orElse(null) == null) {
                if (resourceRepository
                        .countAllByWorkspaceEntityResource_IdAndTeamEntityResource_Id
                                (teamEntity.getWorkspaceEntityTeam().getId(), teamEntity.getId()) == 0) {
                    for (PositionEntity positionEntity : positionRepository.findAllByTeamEntity_Id(teamEntity.getId())) {
                        positionRepository.deleteById(positionEntity.getId());
                    }
                    teamRepository.deleteById(teamEntity.getId());
                } else {
                    teamEntity.setIsArchived(true);
                    for (PositionEntity positionEntity : positionRepository.findAllByTeamEntity_Id(teamEntity.getId())) {
                        if (resourceRepository
                                .countResourcesOfPosition
                                        (positionEntity.getId(), positionEntity.getTeamEntity().getWorkspaceEntityTeam().getId()) == 0) {
                            positionRepository.deleteById(positionEntity.getId());
                        } else {
                            positionEntity.setIsArchived(true);
                            positionRepository.save(positionEntity);
                        }
                    }
                    teamRepository.save(teamEntity);
                }
            }

        }
    }

    private void deletePosition(List<PositionRequest> positionRequests, List<PositionEntity> positionEntities) {

        for (PositionEntity positionEntity : positionEntities) {
            if (positionRequests.stream().filter(s -> positionEntity.getName().equals(s.getName())).findAny().orElse(null) == null) {
                if (positionEntity != null) {
                    if (resourceRepository
                            .countResourcesOfPosition
                                    (positionEntity.getId(), positionEntity.getTeamEntity().getWorkspaceEntityTeam().getId()) == 0) {
                        positionRepository.delete(positionEntity);
                    } else {
                        positionEntity.setIsArchived(true);
                        positionRepository.save(positionEntity);
                    }
                }
            }
        }
    }


    @Override
    public void deleteMultipleTeam(List<TeamRequest> teamRequests) {
        List<TeamEntity> teamEntities = new ArrayList<>();
        for (TeamRequest teamRequest : teamRequests) {
            TeamEntity teamEntity = teamRepository.findById(teamRequest.getId()).orElse(null);
            if (teamEntity != null) {
                teamEntities.add(teamEntity);
            }
        }
        for (TeamEntity teamEntity : teamEntities) {
            if (resourceRepository
                    .countAllByWorkspaceEntityResource_IdAndTeamEntityResource_Id
                            (teamEntity.getWorkspaceEntityTeam().getId(), teamEntity.getId()) == 0) {
                for (PositionEntity positionEntity : teamEntity.getPositionEntities()) {
                    positionRepository.deleteById(positionEntity.getId());
                }
                teamRepository.deleteById(teamEntity.getId());
            } else {
                teamEntity.setIsArchived(true);
                for (PositionEntity positionEntity : teamEntity.getPositionEntities()) {
                    positionEntity.setIsArchived(true);
                    positionRepository.save(positionEntity);
                }
                teamRepository.save(teamEntity);
            }
        }
    }

    private List<TeamDTO> toDTO(List<TeamRequest> teamRequests) {
        List<TeamDTO> teamDTOS = new ArrayList<>();
        for (TeamRequest teamRequest : teamRequests) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setName(teamRequest.getName());
            List<PositionDTO> positionDTOS = new ArrayList<>();
            for (PositionRequest positionRequest : teamRequest.getPositions()) {
                PositionDTO position = new PositionDTO();
                position.setName(positionRequest.getName());
                positionDTOS.add(position);
            }
            teamDTO.setPositionDTOS(positionDTOS);
            teamDTOS.add(teamDTO);
        }
        return teamDTOS;
    }

    private List<TeamDTO> EntitytoDTO(List<TeamEntity> teamEntities) {
        List<TeamDTO> teamDTOS = new ArrayList<>();
        for (TeamEntity teamEntity : teamEntities) {
            TeamDTO teamDTO = new TeamDTO();
            teamDTO.setName(teamEntity.getName());
            List<PositionDTO> positionDTOS = new ArrayList<>();
            for (PositionEntity positionEntity : teamEntity.getPositionEntities()) {
                PositionDTO position = new PositionDTO();
                position.setName(positionEntity.getName());
                positionDTOS.add(position);
            }
            teamDTO.setPositionDTOS(positionDTOS);
            teamDTOS.add(teamDTO);
        }
        return teamDTOS;
    }

    private static boolean listEquals(List<TeamDTO> list1, List<TeamDTO> list2) {
        if (list1.size() != list2.size())
            return true;

        for (TeamDTO teamDTO : list1) {
            if (!list2.contains(teamDTO))
                return true;
        }
        return false;
    }
}
