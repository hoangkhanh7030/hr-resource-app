package com.ces.intern.hr.resourcing.demo.converter;

import com.ces.intern.hr.resourcing.demo.dto.AccountDTO;
import com.ces.intern.hr.resourcing.demo.dto.RoleDTO;
import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountEntity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AccountConverter {
    public AccountEntity toEntity(AccountDTO accountDTO){
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountDTO.getId());
        accountEntity.setEmail(accountDTO.getEmail());
        accountEntity.setFullname(accountDTO.getFullname());
        accountEntity.setAvatar(accountDTO.getAvatar());
        return accountEntity;
    }
    public AccountDTO toDTO(AccountEntity accountEntity){
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setId(accountEntity.getId());
        accountDTO.setEmail(accountEntity.getEmail());
        accountDTO.setFullname(accountEntity.getFullname());
        accountDTO.setRoles(roleDTOList(accountEntity));
        accountDTO.setWorkspaceDto(workspaceDTOList(accountEntity));
        return accountDTO;


    }


    public List<WorkspaceDTO> workspaceDTOList(AccountEntity accountEntity){
        List<WorkspaceDTO> list = new ArrayList<>();
        for (int i=0;i<accountEntity.getEntityAccoutWorkspaceRoleList().size();i++){
            WorkspaceDTO workspaceDTO = new WorkspaceDTO();
            workspaceDTO.setId(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getId());
            workspaceDTO.setName(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getWorkspaceEntity().getName());
            list.add(workspaceDTO);
        }
        return list;
    }
    public List<RoleDTO> roleDTOList(AccountEntity accountEntity){
        List<RoleDTO> list = new ArrayList<>();
        for(int i=0;i<accountEntity.getEntityAccoutWorkspaceRoleList().size();i++){
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getRoleEntity().getId());
            roleDTO.setName(accountEntity.getEntityAccoutWorkspaceRoleList().get(i).getRoleEntity().getName());
            list.add(roleDTO);
        }
        return list;
    }
}
