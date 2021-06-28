package com.ces.intern.hr.resourcing.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "time")
public class TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private ResourceEntity resourceEntity;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity projectEntity;
}
