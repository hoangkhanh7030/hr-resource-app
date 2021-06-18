package com.ces.intern.hr.resourcing.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project")
public class ProjectEntity extends BaseEnity{
    @Column(name = "name")
    private String name;
    @Column(name = "color")
    private String color;
    @Column(name = "is_activate")
    private Boolean isActivate;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "projectEntity")
    private List<TimeEntity> timeEntities = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspaceEntityProject;
}
