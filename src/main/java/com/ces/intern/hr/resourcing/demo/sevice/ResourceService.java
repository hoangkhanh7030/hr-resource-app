package com.ces.intern.hr.resourcing.demo.sevice;

import com.ces.intern.hr.resourcing.demo.dto.ResourceDTO;
import com.ces.intern.hr.resourcing.demo.http.request.ResourceRequest;
import com.ces.intern.hr.resourcing.demo.http.response.MessageResponse;

import java.util.List;
<<<<<<< HEAD
=======

>>>>>>> 5c73417917891f9cb644177ad1d9c0b8d84f0b29

public interface ResourceService {
    List<ResourceDTO> searchByName(String name, String posName, String teamName,
                                   Integer workspaceId, Integer page, Integer size);

    MessageResponse addNewResource(ResourceRequest resourceRequest, Integer id, Integer accountId);

    ResourceDTO findById(Integer id);

    MessageResponse updateResource(ResourceRequest resourceRequest, Integer resourceId, Integer workspaceId, Integer accountId);

    MessageResponse deleteResource(Integer id, Integer workspaceId);

    List<ResourceDTO> getResourcesOfWorkSpace(Integer id, Integer page, Integer size);

    List<ResourceDTO> getProductManagers(Integer id);

    List<ResourceDTO> getAccountManagers(Integer id);

    ResourceDTO getResourceInfo(Integer resourceId, Integer workspaceId);

    List<ResourceDTO> filterByTeam(Integer id, String teamName, Integer page, Integer size);

    List<ResourceDTO> filterByPosition(Integer id, String posName, Integer page, Integer size);

    List<ResourceDTO> filterByTeamAndPosition(Integer id, String teamName, String posName, Integer page, Integer size);
}
