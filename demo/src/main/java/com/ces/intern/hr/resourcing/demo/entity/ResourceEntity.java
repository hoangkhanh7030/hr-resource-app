package com.ces.intern.hr.resourcing.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resource")
public class ResourceEntity extends BaseEnity{
    @Column(name = "name")
    private String name;
    @Column(name = "avatar")
    private String avatar;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity teamEntity;
    @ManyToOne
    @JoinColumn(name = "position_id")
    private PositionEnitity positionEnitity;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspaceEntityResource;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "resourceEntity")
    private List<TimeEntity> timeEntities;
}
