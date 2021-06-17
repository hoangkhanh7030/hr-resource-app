package com.ces.intern.hr.resourcing.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "workspace")
public class WorkspaceEntity extends BaseEnity{
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "workspaceEntity")

    private List<AccountWorkspaceRoleEntity> entityList;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "workspaceEntityResource")
    private List<ResourceEntity> resourceEntities;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "workspaceEntityProject")

    private List<ProjectEntity> projectEntities;



}
