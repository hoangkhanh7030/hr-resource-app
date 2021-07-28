package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;
import com.ces.intern.hr.resourcing.demo.http.response.ResourceListResponse;

import java.util.List;

public interface ResourceService {
    List<ResourceDTO> searchByName(String name, String posName, String teamName,
                                   Integer workspaceId, Integer page, Integer size);

    MessageResponse addNewResource(ResourceRequest resourceRequest, Integer accountId);

    ResourceDTO findById(Integer id);

    MessageResponse updateResource(ResourceRequest resourceRequest, Integer resourceId, Integer workspaceId, Integer accountId);

    MessageResponse archiveResource(Integer resourceId, Integer workspaceId);

    MessageResponse deleteResource(Integer id, Integer workspaceId);

    List<ResourceDTO> getResourcesOfWorkSpace(Integer id);

    ResourceDTO getResourceInfo(Integer resourceId, Integer workspaceId);

//    List<ResourceDTO> sortResources(Integer idWorkspace, String searchName, String teamName, String posName,
//                                    String sortColumn, String type, Integer page, Integer size);

    List<ResourceDTO> sortResources(Integer idWorkspace, String searchName, String isArchived,
                                    String sortColumn, String type, Integer page, Integer size);

//    Integer getNumberOfResources(Integer idWorkspace, String searchName, String teamName, String posName);

    Integer getNumberOfResources(Integer idWorkspace, String searchName, String isArchived);

}
