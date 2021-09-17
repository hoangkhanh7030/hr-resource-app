package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.http.request.ProjectRequest;
import com.ces.intern.hr.resourcing.demo.http.response.message.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.project.NumberSizeResponse;
import com.ces.intern.hr.resourcing.demo.importCSV.Message.Response;
import com.ces.intern.hr.resourcing.demo.sevice.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/workspaces")
public class ProjectController {

    private final ProjectService projectService;


    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;

    }


    @GetMapping(value = "/{idWorkspace}/projects/export")
    public MessageResponse exportToCSV(HttpServletResponse response,
                                       @PathVariable Integer idWorkspace) {
        return projectService.export(response, idWorkspace);
    }

    @PostMapping(value = "/{idWorkspace}/projects/import")
    public Response importCsvFile(@RequestHeader("AccountId") Integer idAccount,
                                  @PathVariable Integer idWorkspace,
                                  @RequestParam("csvfile") MultipartFile csvfile) {
        return projectService.importCSV(idAccount, idWorkspace, csvfile);
    }

    @PostMapping(value = "/{idWorkspace}/projects")
    private MessageResponse createdProject(@RequestHeader("AccountId") Integer idAccount,
                                           @PathVariable Integer idWorkspace,
                                           @RequestBody ProjectRequest projectRequest) {

        return projectService.createdProject(projectRequest, idAccount, idWorkspace);

    }

    @PutMapping(value = "/{idWorkspace}/projects/{idProject}")
    private MessageResponse updateProject(@RequestHeader("AccountId") Integer idAccount,
                                          @PathVariable Integer idWorkspace,
                                          @PathVariable Integer idProject,
                                          @RequestBody ProjectRequest projectRequest) {
        return projectService.updateProject(projectRequest, idAccount, idProject, idWorkspace);
    }

    @DeleteMapping(value = "{idWorkspace}/projects/{idProject}")
    private MessageResponse deleteProject(@PathVariable Integer idProject,
                                          @PathVariable Integer idWorkspace) {
        return projectService.deleteProject(idProject, idWorkspace);

    }


    @PutMapping(value = "/{idWorkspace}/projects/{idProject}/isActivate")
    private MessageResponse isActivate(@PathVariable Integer idWorkspace,
                                       @PathVariable Integer idProject) {
        return projectService.isActivate(idProject, idWorkspace);


    }

    @GetMapping(value = "/{idWorkspace}/projects")
    private NumberSizeResponse getAll(@PathVariable Integer idWorkspace,
                                      @RequestParam int page,
                                      @RequestParam int size,
                                      @RequestParam String sortName,
                                      @RequestParam String searchName,
                                      @RequestParam String type,
                                      @RequestParam String isActivate) {

        return projectService.ListProject(idWorkspace, page, size, sortName, searchName, type, isActivate);

    }
}