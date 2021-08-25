package com.ces.intern.hr.resourcing.demo.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter

@AllArgsConstructor
@NoArgsConstructor
public enum AuthenticationProvider {
    LOCAL("LOCAL"),GOOGLE("GOOGLE"),
    PENDING("PENDING"),INACTIVE("INACTIVE");
    private String name;

}
