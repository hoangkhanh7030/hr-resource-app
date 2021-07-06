package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.WorkspaceResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.repository.WorkspaceRepository;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import com.ces.intern.hr.resourcing.demo.utils.ExceptionMessage;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Role;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class WorkspaceController {


    private final WorkspaceService workspaceService;
    private final WorkspaceRepository workspaceRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;

    @Autowired
    public WorkspaceController(WorkspaceService workspaceService,
                               WorkspaceRepository workspaceRepository,
                               AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository) {

        this.workspaceService = workspaceService;
        this.workspaceRepository = workspaceRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }

    @GetMapping(value = "/{idWorkspace}")
    private WorkspaceDTO getWorkspace(@RequestHeader("AccountId") Integer idAccount, @PathVariable Integer idWorkspace) {
        return workspaceService.getWorkspace(idWorkspace, idAccount);
    }

    @GetMapping(value = "")
    private List<WorkspaceResponse> getAllWorkspace(@RequestHeader(value = "AccountId") Integer idAccount) {


        return workspaceService.getAllWorkspaceByIdAccount(idAccount);
    }

    @PostMapping(value = "")
    private MessageResponse createWorkspaceByIdAccount(@RequestHeader("AccountId") Integer idAccount, @RequestBody WorkspaceDTO workspaceDTO) {

        if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {

            return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
        } else {
            if (workspaceDTO.getName().isEmpty() || workspaceDTO.getName() == null) {
                return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
            } else {
                workspaceService.createdWorkspaceByIdAccount(workspaceDTO, idAccount);
            }
            if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {
                return new MessageResponse(ResponseMessage.CREATE_SUCCESS, Status.SUCCESS.getCode());

            }
            return new MessageResponse(ResponseMessage.CREATE_FAIL, Status.FAIL.getCode());
        }
    }

    @PutMapping(value = "/{idWorkspace}")
    private MessageResponse updateWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                         @RequestBody WorkspaceDTO workspaceDTO,
                                                         @RequestHeader("AccountId") Integer idAccount) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));

        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {
                return new MessageResponse(ResponseMessage.ALREADY_EXIST, Status.FAIL.getCode());
            } else {
                if (workspaceDTO.getName().isEmpty() || workspaceDTO.getName() == null) {
                    return new MessageResponse(ResponseMessage.IS_EMPTY, Status.FAIL.getCode());
                } else {

                    workspaceService.updateWorkspaceByIdWorkspace(workspaceDTO, idWorkspace, idAccount);
                }

                if (accoutWorkspaceRoleRepository.findByNameWorkspaceAndIdAccount(workspaceDTO.getName(), idAccount).isPresent()) {
                    return new MessageResponse(ResponseMessage.UPDATE_SUCCESS, Status.SUCCESS.getCode());
                }
                return new MessageResponse(ResponseMessage.UPDATE_FAIL, Status.FAIL.getCode());
            }

        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping(value = "/{idWorkspace}")
    private MessageResponse deleteWorkspaceByIdWorkspace(@PathVariable Integer idWorkspace,
                                                         @RequestHeader("AccountId") Integer idAccount) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId(idWorkspace, idAccount)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            workspaceService.deleteWorkspaceByIdWorkspace(idWorkspace, idAccount);
            if (workspaceRepository.findById(idWorkspace).isPresent()) {
                return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());

            }
            return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
        } else return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @GetMapping(value = "/search/{name}")
    private List<WorkspaceDTO> search(@PathVariable String name) {
        return workspaceService.searchWorkspaceByName(name);
    }


}
