package com.ces.intern.hr.resourcing.demo.http.response;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NumberSizeResponse {
    private List<ProjectDTO> projectDTOList;
    private Integer numberSize;
}
