package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.TeamResponse;

import java.util.List;

public interface TeamService {
    List<TeamDTO> getAll(Integer idWorkspace);


    MessageResponse deleteTeam(Integer idTeam);
    MessageResponse renameTeam(Integer idWorkspace,Integer idTeam,TeamDTO teamDTO);

    List<TeamResponse> getTeams(Integer idWorkspace);
    MessageResponse created(List<TeamRequest> teamRequests, Integer idWorkspace);
    MessageResponse update(List<TeamRequest> teamRequests, Integer idWorkspace);


    void deleteMultipleTeam(List<TeamRequest> teamRequests);
}
