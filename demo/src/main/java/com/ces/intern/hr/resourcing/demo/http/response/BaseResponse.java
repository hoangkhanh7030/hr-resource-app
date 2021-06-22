package com.ces.intern.hr.resourcing.demo.http.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BaseResponse {
    private Integer id;
    private Date createdDate;
    private Integer createdBy;
    private Date modifiedDate;
    private Integer modifiedBy;

}
