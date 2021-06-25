package com.ces.intern.hr.resourcing.demo.controller;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.entity.ResourceEntity;
import com.ces.intern.hr.resourcing.demo.sevice.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    @PostMapping(value = "/createResource")
    private ResourceDTO createdResource(@RequestBody ResourceDTO resourceDTO) {
        return resourceService.createResource(resourceDTO);
    }


}
