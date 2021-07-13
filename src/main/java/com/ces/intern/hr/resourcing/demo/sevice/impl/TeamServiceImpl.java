package com.ces.intern.hr.resourcing.demo.sevice.impl;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.entity.TeamEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.repository.ResourceRepository;
import com.ces.intern.hr.resourcing.demo.repository.TeamRepository;
import com.ces.intern.hr.resourcing.demo.sevice.TeamService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final ModelMapper modelMapper;
    private final ResourceRepository resourceRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository,
                           ModelMapper modelMapper,
                           ResourceRepository resourceRepository) {
        this.teamRepository = teamRepository;
        this.modelMapper = modelMapper;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public List<TeamDTO> getAll() {
        List<TeamEntity> teamEntityList = teamRepository.findAll();
        return teamEntityList.stream().map(s -> modelMapper.map(s, TeamDTO.class)).collect(Collectors.toList());
    }

    @Override
    public void addResourceToTeam(Integer idTeam, Integer idResource) {
        ResourceEntity resourceEntity = resourceRepository.findById(idResource)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        TeamEntity teamEntity = teamRepository.findById(idTeam)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        resourceEntity.setTeamEntity(teamEntity);
        resourceRepository.save(resourceEntity);

    }

    @Override
    public void deleteTeam(Integer idTeam) {
        TeamEntity teamEntity = teamRepository.findById(idTeam)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        teamRepository.delete(teamEntity);
    }

    @Override
    public void renameTeam(Integer idTeam, String name) {
        TeamEntity teamEntity = teamRepository.findById(idTeam)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        teamEntity.setName(name);
        teamRepository.save(teamEntity);

    }

    @Override
    public void updateTeam(List<TeamDTO> teamDTOS) {
        List<TeamEntity> teamEntities = teamRepository.findAll();
        deleteTeam(teamDTOS,teamEntities);
        for (TeamDTO teamDTO : teamDTOS){
            if (!teamRepository.findByName(teamDTO.getName()).isPresent()){
                TeamEntity teamEntity = new TeamEntity();
                teamEntity.setName(teamDTO.getName());
                teamRepository.save(teamEntity);
            }
        }

    }
    private void deleteTeam(List<TeamDTO> teamDTOS, List<TeamEntity> teamEntities){

        for (TeamEntity teamEntity: teamEntities){
            if (teamDTOS.stream().filter(s->teamEntity.getName().equals(s.getName())).findAny().orElse(null)==null){
                teamRepository.delete(teamEntity);
            }
        }
    }


}
