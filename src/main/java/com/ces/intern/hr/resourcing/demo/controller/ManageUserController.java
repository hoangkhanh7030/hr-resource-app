package com.ces.intern.hr.resourcing.demo.controller;
import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.user.ManageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ManageUserService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;


@RestController
@RequestMapping("api/v1/workspaces")
public class ManageUserController {
    private final ManageUserService manageUserService;
    private final AccoutRepository accoutRepository;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;

    @Autowired
    public ManageUserController(ManageUserService manageUserService,
                                AccoutRepository accoutRepository,
                                AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository) {
        this.manageUserService = manageUserService;
        this.accoutRepository = accoutRepository;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
    }

    @GetMapping("/{idWorkspace}/manageUsers")
    private ManageResponse getAll(@PathVariable Integer idWorkspace,
                                  @RequestParam Integer page,
                                  @RequestParam Integer size,
                                  @RequestParam String searchName,
                                  @RequestParam String sortName,
                                  @RequestParam String type) {
        if (sortName == null || sortName.isEmpty()) {
            sortName = "create_date";
        }
        searchName = searchName == null ? "" : searchName;
        int sizeList = accoutRepository.findAllBysearchNameToList(idWorkspace, searchName).size();

        return new ManageResponse(manageUserService.getAll(idWorkspace, page, size, searchName, sortName, type),
                numberSize(sizeList, size));
    }

    @DeleteMapping("/{idWorkspace}/account/{idAccount}")
    private MessageResponse delete(@PathVariable Integer idWorkspace,
                                   @PathVariable Integer idAccount) {
        manageUserService.delete(idAccount, idWorkspace);
        if (accoutRepository.findById(idAccount).isPresent()) {
            return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_SUCCESS, Status.SUCCESS.getCode());
    }

    @PostMapping("/{idWorkspace}/reinvited")
    private MessageResponse send(@RequestBody ReInviteRequest reInviteRequest,
                                 @PathVariable Integer idWorkspace
    ) {
        try {
            manageUserService.reSendEmail(reInviteRequest, idWorkspace);
            return new MessageResponse(ResponseMessage.EMAIL_SENDT, Status.SUCCESS.getCode());
        } catch (Exception e) {
            return new MessageResponse(ResponseMessage.EMAIL_ERROR + e, Status.FAIL.getCode());
        }


    }

    @PutMapping("/{idWorkspace}/isActive/{idAccount}")
    private MessageResponse isActive(@PathVariable Integer idAccount,
                                     @PathVariable Integer idWorkspace,
                                     @RequestBody String url) throws MessagingException, IOException {
        try {
            manageUserService.isActive(idAccount, idWorkspace,url);
            return new MessageResponse(ResponseMessage.EMAIL_SENDT, Status.SUCCESS.getCode());
        } catch (Exception e) {
            return new MessageResponse(ResponseMessage.EMAIL_ERROR + e, Status.FAIL.getCode());
        }
    }

    private int numberSize(int sizeList, int size) {
        int numberSize;
        if (sizeList % size == 0) {
            numberSize = sizeList / size;
        } else {
            numberSize = (sizeList / size) + 1;
        }
        return numberSize;
    }

}
