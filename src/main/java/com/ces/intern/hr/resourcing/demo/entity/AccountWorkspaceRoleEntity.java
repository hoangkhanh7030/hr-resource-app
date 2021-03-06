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
@Table(name = "account_workspace_role")
public class AccountWorkspaceRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role")
    private Integer codeRole;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;
    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspaceEntity;



}
