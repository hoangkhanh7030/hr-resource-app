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
@Table(name = "position")
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity teamEntity;

    @OneToMany(cascade = {
            CascadeType.DETACH,CascadeType.MERGE,
            CascadeType.PERSIST,CascadeType.REFRESH
    },mappedBy = "positionEntity")
    private List<ResourceEntity> resourceEntities = new ArrayList<>();
    @PreRemove
    private void preRemove() {
        resourceEntities.forEach( child -> child.setPositionEntity(null));
    }
}
