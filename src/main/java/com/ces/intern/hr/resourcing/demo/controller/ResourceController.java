package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.AccountWorkspaceRoleEntity;
import com.ces.intern.hr.resourcing.demo.http.exception.NotFoundException;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceListResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.ApacheCommonsCsvUtil;
import com.ces.intern.hr.resourcing.demo.importCSV.CsvFileService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/workspaces")
public class ResourceController {
    private final ResourceService resourceService;
    private final AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository;
    private final CsvFileService csvFileService;

    @Autowired
    private ResourceController(ResourceService resourceService,
                               AccoutWorkspaceRoleRepository accoutWorkspaceRoleRepository,
                               CsvFileService csvFileService
    ) {
        this.resourceService = resourceService;
        this.accoutWorkspaceRoleRepository = accoutWorkspaceRoleRepository;
        this.csvFileService = csvFileService;
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
        if (type == null || type.isEmpty()){
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
        int listSize = resourceService.getNumberOfResources(workspaceId, keyword, teamName, posName);
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
        response.setContentType(CSVFile.CONTENT_TYPE);
        DateFormat dateFormat = new SimpleDateFormat(CSVFile.DATE);
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = CSVFile.HEADER_KEY;
        String headerValue = CSVFile.HEADER_VALUE_RESOURCE + currentDateTime + CSVFile.FILE_TYPE;
        response.setHeader(headerKey, headerValue);
        List<ResourceDTO> resourceDTOS = resourceService.getResourcesOfWorkSpace(workspaceId);
        List<ResourceRequest> resourceRequests = new ArrayList<>();

        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = CSVFile.CSV_HEADER_RESOURCE;
        String[] nameMapping = CSVFile.NAME_MAPPING_RESOURCE;

        csvWriter.writeHeader(csvHeader);
        for (ResourceDTO resourceDTO : resourceDTOS) {
            ResourceRequest resourceRequest = new ResourceRequest();
            resourceRequest.setId(resourceDTO.getId());
            resourceRequest.setName(resourceDTO.getName());
            resourceRequest.setAvatar(resourceDTO.getAvatar());
            //resourceRequest.setTeamId(resourceDTO.getPositionDTO().getId());
            resourceRequest.setPositionId(resourceDTO.getPositionDTO().getId());
            resourceRequests.add(resourceRequest);
        }
        for (ResourceRequest resourceRequest : resourceRequests) {
            csvWriter.write(resourceRequest, nameMapping);
        }

        csvWriter.close();
    }

    @PostMapping("/{workspaceId}/resources/import")
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
            csvFileService.storeResource(csvFile.getInputStream(), workspaceId, idAccount);
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
            resourceRequest.setName(resourceRequest.getName() == null? "" : resourceRequest.getName());
            resourceRequest.setPositionId(resourceRequest.getPositionId() == null? 0 : resourceRequest.getPositionId());
            return resourceService.addNewResource(resourceRequest, accountId);
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
            resourceRequest.setId(resourceId);
            resourceRequest.setName(resourceRequest.getName() == null? "" : resourceRequest.getName());
            resourceRequest.setPositionId(resourceRequest.getPositionId() == null? 0 : resourceRequest.getPositionId());
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
