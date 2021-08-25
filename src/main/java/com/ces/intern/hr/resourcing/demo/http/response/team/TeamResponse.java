package com.ces.intern.hr.resourcing.demo.http.response.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private Integer id;
    private String name;
    private List<PositionResponse> positions;
}
