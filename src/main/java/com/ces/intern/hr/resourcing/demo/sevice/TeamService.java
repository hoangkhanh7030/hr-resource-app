package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;
import com.ces.intern.hr.resourcing.demo.http.request.TeamRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.TeamResponse;

import java.util.List;

public interface TeamService {
    List<TeamDTO> getAll(Integer idWorkspace);
    void addResourceToTeam(Integer idTeam,Integer idResource);

    void deleteTeam(Integer idTeam);
    void renameTeam(Integer idTeam,String name);

    List<TeamResponse> getTeams(Integer idWorkspace);
    void created(List<TeamRequest> teamRequests, Integer idWorkspace);
    void update(List<TeamRequest> teamRequests, Integer idWorkspace);
}
