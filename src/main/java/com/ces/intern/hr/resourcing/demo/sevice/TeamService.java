package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.TeamDTO;

import java.util.List;

public interface TeamService {
    List<TeamDTO> getAll();
    void addResourceToTeam(Integer idTeam,Integer idResource);

    void deleteTeam(Integer idTeam);
    void renameTeam(Integer idTeam,String name);
}
