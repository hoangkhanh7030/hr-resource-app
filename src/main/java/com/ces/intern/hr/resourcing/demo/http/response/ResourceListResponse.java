package com.ces.intern.hr.resourcing.demo.http.response;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceListResponse {
    private List<ResourceDTO> list;
    private Integer pageSize;
}
