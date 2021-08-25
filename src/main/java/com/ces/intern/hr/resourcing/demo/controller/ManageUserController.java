package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.http.request.ReInviteRequest;
import com.ces.intern.hr.resourcing.demo.http.response.ManageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.repository.AccoutRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ManageUserService;
import com.ces.intern.hr.resourcing.demo.utils.ResponseMessage;
import com.ces.intern.hr.resourcing.demo.utils.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;

@RestController
@RequestMapping("api/v1/workspaces")
public class ManageUserController {
    private final ManageUserService manageUserService;
    private final AccoutRepository accoutRepository;

    @Autowired
    public ManageUserController(ManageUserService manageUserService,
                                AccoutRepository accoutRepository) {
        this.manageUserService = manageUserService;
        this.accoutRepository = accoutRepository;
    }

    @GetMapping("/{idWorkspace}/manageUsers")
    private ManageResponse getAll(@PathVariable Integer idWorkspace,
                                  @RequestParam Integer page,
                                  @RequestParam Integer size,
                                  @RequestParam String searchName,
                                  @RequestParam String sortName,
                                  @RequestParam String type) {
        if (sortName==null||sortName.isEmpty()){
            sortName="create_date";
        }
        searchName = searchName == null ? "" : searchName;
        int sizeList = accoutRepository.findAllBysearchNameToList(idWorkspace, searchName).size();

        return new ManageResponse(manageUserService.getAll(idWorkspace, page, size, searchName, sortName, type),
                numberSize(sizeList, size));
    }
    @DeleteMapping("/{idWorkspace}/account/{idAccount}")
    private MessageResponse delete(@PathVariable Integer idWorkspace,
                                   @PathVariable Integer idAccount){
        manageUserService.delete(idAccount,idWorkspace);
        if (accoutRepository.findById(idAccount).isPresent()){
            return new MessageResponse(ResponseMessage.DELETE_FAIL, Status.FAIL.getCode());
        }
        return new MessageResponse(ResponseMessage.DELETE_SUCCESS,Status.SUCCESS.getCode());
    }

    @PostMapping("/reinvited")
    private MessageResponse send(@RequestBody ReInviteRequest reInviteRequest
    )  {
        try {
            manageUserService.sendEmail(reInviteRequest);
            return new MessageResponse(ResponseMessage.EMAIL_SENDT, Status.SUCCESS.getCode());
        } catch (Exception e) {
            return new MessageResponse(ResponseMessage.EMAIL_ERROR + e, Status.FAIL.getCode());
        }
    }
    @PutMapping("/isActive/{idAccount}")
    private MessageResponse isActive(@PathVariable Integer idAccount){
        manageUserService.isActive(idAccount);
        return new MessageResponse(ResponseMessage.UPDATE_SUCCESS,Status.SUCCESS.getCode());
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
