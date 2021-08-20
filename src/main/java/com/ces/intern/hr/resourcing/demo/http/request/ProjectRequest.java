package com.ces.intern.hr.resourcing.demo.http.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest {
    private String name;
    private String clientName;
    private String color;
    private String textColor;
    private String colorPattern;
    private Boolean isActivate;

    public boolean validate() {
        if (name == null||name.isEmpty()|| clientName == null || clientName.isEmpty() ||
                color == null  || color.isEmpty() || textColor == null || textColor.isEmpty()
                  ) {
            return true;
        } else {
            return false;
        }
    }

}
