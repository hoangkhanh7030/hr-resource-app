package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ProjectDTO;
import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceListResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.ApacheCommonsCsvUtil;
import com.ces.intern.hr.resourcing.demo.importCSV.CsvFileSerivce;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Message;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import com.ces.intern.hr.resourcing.demo.repository.AccoutWorkspaceRoleRepository;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import com.ces.intern.hr.resourcing.demo.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/workspaces")
public class ResourceController {
    private final ResourceService resourceService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final CsvFileSerivce csvFileSerivce;
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    @Autowired
    private ResourceController(ResourceService resourceService,
                               AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                               CsvFileSerivce csvFileSerivce
    ) {
        this.resourceService = resourceService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.csvFileSerivce = csvFileSerivce;
    }


    @GetMapping("/{workspaceId}/resources")
    public ResourceListResponse showResourceList(@PathVariable Integer workspaceId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String keyword,
                                                 @RequestParam String teamName,
                                                 @RequestParam String posName,
                                                 @RequestParam String sortColumn,
                                                 @RequestParam String type) {
        teamName = teamName == null ? "" : teamName;
        posName = posName == null ? "" : posName;
        keyword = keyword == null ? "" : keyword;
        sortColumn = sortColumn == null ? "" : sortColumn;
        //type = type == null ? SortPara.DESC.getName() : type;
        if (type == null){
            type = SortPara.DESC.getName();
        }
        else{
            if (!type.equals(SortPara.DESC.getName()) && !type.equals(SortPara.ASC.getName())){
                type = SortPara.DESC.getName();
            }
        }
        System.out.println(teamName + posName + sortColumn + type);
        List<ResourceDTO> resourceDTOList = resourceService
                .sortResources(workspaceId, keyword, teamName, posName, sortColumn, type, page, size);
        int listSize = resourceService.getResourcesOfWorkSpace(workspaceId).size();
        int numberOfPages;
        if (listSize == 0) {
            numberOfPages = 0;
        } else {
            if (listSize % size == 0) {
                numberOfPages = listSize / size;
            } else {
                numberOfPages = (listSize / size) + 1;
            }
        }
        return new ResourceListResponse(resourceDTOList, numberOfPages);
    }


    @GetMapping("/{workspaceId}/resources/export")
    public void exportCSV(HttpServletResponse response,
                          @PathVariable Integer workspaceId
    ) throws IOException {
        response.setContentType("text/csv");
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=project_" + currentDateTime + ".csv";
        response.setHeader(headerKey, headerValue);
        List<ResourceDTO> resourceDTOS = resourceService.getResourcesOfWorkSpace(workspaceId);

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Resource ID", "Name", "Workspace Name", "Team", "Position", "Bookings"};
        String[] nameMapping = {"id", "name", "workspaceName", "teamDTO", "positionDTO", "times"};

        csvWriter.writeHeader(csvHeader);

        for (ResourceDTO resourceDTO : resourceDTOS) {
            csvWriter.write(resourceDTO, nameMapping);
        }

        csvWriter.close();
    }

    @GetMapping("/{workspaceId}/resources/import")
    public Response importCSV(@RequestHeader("AccountId") Integer idAccount,
                              @PathVariable Integer workspaceId,
                              @RequestParam("csvFile") MultipartFile csvFile
    ){
        Response response = new Response();
        if (Objects.requireNonNull(csvFile.getOriginalFilename()).isEmpty()) {
            response.addMessage(new Message(csvFile.getOriginalFilename(),
                    "No selected file to upload! Please do the checking", Status.FAIL.getCode()));

            return response;
        }
        if (!ApacheCommonsCsvUtil.isCSVFile(csvFile)) {
            response.addMessage(new Message(csvFile.getOriginalFilename(), "Error: this is not a CSV file!", Status.FAIL.getCode()));
            return response;
        }


        try {
            csvFileSerivce.store(csvFile.getInputStream(), workspaceId, idAccount);
            response.addMessage(new Message(csvFile.getOriginalFilename(), "Upload File Successfully!", Status.SUCCESS.getCode()));
        } catch (Exception e) {
            response.addMessage(new Message(csvFile.getOriginalFilename(), e.getMessage(), Status.FAIL.getCode()));
        }

        return response;

    }

    @PostMapping("/{workspaceId}/resources")
    public MessageResponse createResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            //resourceService.addNewResource(resourceRequest, workspaceId, accountId);
            return resourceService.addNewResource(resourceRequest, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @GetMapping("/{workspaceId}/resources/search")
    public List<ResourceDTO> searchResource(@RequestParam String name,
                                            @RequestParam String posName,
                                            @RequestParam String teamName,
                                            @PathVariable Integer workspaceId,
                                            @RequestParam Integer page,
                                            @RequestParam Integer size) {
        return resourceService.searchByName(name, posName, teamName, workspaceId, page, size);
    }

    @GetMapping("/{workspaceId}/resources/{resourceId}")
    public ResourceDTO getOneResourceInfo(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId) {
        return resourceService.getResourceInfo(resourceId, workspaceId);
    }

    @PutMapping("/{workspaceId}/resources/{resourceId}")
    public MessageResponse updateResource(@RequestBody ResourceRequest resourceRequest,
                                          @PathVariable Integer workspaceId,
                                          @PathVariable Integer resourceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return resourceService.updateResource(resourceRequest, resourceId, workspaceId, accountId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

    @DeleteMapping("/{workspaceId}/resources/{resourceId}")
    public MessageResponse deleteResource(@PathVariable Integer resourceId,
                                          @PathVariable Integer workspaceId,
                                          @RequestHeader Integer accountId) {
        AccountWorkspaceRoleEntity accountWorkspaceRoleEntity = accoutWorkspaceRoleRepository.findByIdAndId
                (workspaceId, accountId).orElseThrow(() -> new NotFoundException(ExceptionMessage.NOT_FOUND_RECORD.getMessage()));
        if (accountWorkspaceRoleEntity.getCodeRole().equals(Role.EDIT.getCode())) {
            return resourceService.deleteResource(resourceId, workspaceId);
        }
        return new MessageResponse(ResponseMessage.ROLE, Status.FAIL.getCode());
    }

//    @GetMapping(value = "/{workspaceId}/resources/sort")
//    private List<ResourceDTO> sortProject(@PathVariable Integer workspaceId,
//                                         @RequestParam Integer page,
//                                         @RequestParam Integer size,
//                                         @RequestParam String name,
//                                         @RequestParam String type){
//        return resourceService.sortResources(workspaceId, name, type, page, size);
//    }

}
