package com.ces.intern.hr.resourcing.demo.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.SecondaryTable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionResponse {
    private Integer id;
    private String name;
}
