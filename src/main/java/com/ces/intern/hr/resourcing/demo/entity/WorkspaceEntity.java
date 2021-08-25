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
@Table(name = "workspace")
public class WorkspaceEntity extends BaseEnity {
    @Column(name = "name")
    private String name;
    @Column(name = "email_suffix")
    private String emailSuffix;
    @Column(name = "extra_saturday")
    private boolean extraSaturday = false;
    @Column(name = "extra_sunday")
    private boolean extraSunday = false;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "workspaceEntity")
    private List<AccountWorkspaceRoleEntity> entityList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workspaceEntityProject")
    private List<ProjectEntity> projectEntities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workspaceEntityResource")
    private List<ResourceEntity> resourceEntities = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "workspaceEntityTeam")
    private List<TeamEntity> teamEntities = new ArrayList<>();
}
