package com.ces.intern.hr.resourcing.demo.http.response.dashboard;

import com.ces.intern.hr.resourcing.demo.http.response.resource.ResourceResponse;
import com.ces.intern.hr.resourcing.demo.http.response.team.TeamResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardListResponse {

   private List<ResourceResponse> resources;
   private List<TeamResponse> teams;
   private Integer status;
}
