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
@Table(name = "team")
public class TeamEntity extends BaseEnity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspaceEntityTeam;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "teamEntity")
    private List<PositionEntity> positionEntities = new ArrayList<>();
    @OneToMany(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST,CascadeType.REFRESH
    },mappedBy = "teamEntityResource")
    private List<ResourceEntity> resourceEntities = new ArrayList<>();

}
