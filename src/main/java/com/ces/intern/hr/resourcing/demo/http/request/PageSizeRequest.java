package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageSizeRequest {
    private Integer page;
    private Integer size;
}

