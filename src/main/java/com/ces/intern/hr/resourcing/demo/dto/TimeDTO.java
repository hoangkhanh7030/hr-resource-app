package com.ces.intern.hr.resourcing.demo.dto;

import com.ces.intern.hr.resourcing.demo.entity.BaseEnity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeDTO {
    private Integer id;
    private Date startDate;
    private Date endDate;
    private Double percentage;
    private Double duration;
    private ProjectDTO projectDTO;
    private Double hourTotal;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeDTO that = (TimeDTO) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
