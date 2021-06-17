package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.WorkspaceDTO;
import com.ces.intern.hr.resourcing.demo.sevice.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private WorkspaceService workspaceService;
    @RequestMapping(value = "/index")
    public String view(Model model){
        List<WorkspaceDTO> list =workspaceService.getWorkspaces();
        model.addAttribute("list",list);
        return "index";
    }
    @RequestMapping(value = "/login")
    public String login(){
        return "login";
    }
}
