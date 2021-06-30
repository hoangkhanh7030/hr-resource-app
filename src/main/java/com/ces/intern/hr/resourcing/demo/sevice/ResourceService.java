package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;

import java.util.List;
import java.util.Optional;


public interface ResourceService {
    List<ResourceDTO> searchByName(String name, Integer id);

    MessageResponse addNewResource(ResourceRequest resourceRequest, Integer id, Integer accountId);

    ResourceDTO findById(Integer id);

    MessageResponse updateResource(ResourceRequest resourceRequest, Integer resourceId, Integer workspaceId, Integer accountId);

    MessageResponse deleteResource(Integer id, Integer workspaceId);

    List<ResourceDTO> getResourcesOfWorkSpace(Integer id);

    List<ResourceDTO> getProductManagers(Integer id);
    List<ResourceDTO> getAccountManagers(Integer id);

    ResourceDTO getResourceInfo(Integer resourceId, Integer workspaceId);
}
