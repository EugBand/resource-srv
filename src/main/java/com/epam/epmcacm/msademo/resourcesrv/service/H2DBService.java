package com.epam.epmcacm.msademo.resourcesrv.service;

import com.epam.epmcacm.msademo.resourcesrv.entity.Resource;
import com.epam.epmcacm.msademo.resourcesrv.exception.BadRequestException;
import com.epam.epmcacm.msademo.resourcesrv.repository.ResourceRepository;
import com.epam.epmcacm.msademo.resourcesrv.validation.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class H2DBService {

    public static final String NOT_FOUND_IN_BD = "Resource with id %s not found in bd";

    @Autowired ResourceRepository resourceRepository;

    public String addResourceData(String uuid, String fileName) {
        Resource resourceDto = Resource.builder()
                .id(uuid)
                .filePath(fileName)
                .createdAt(Instant.now())
                .build();
        Resource savedResource = resourceRepository.save(resourceDto);
        String savedResourceId = savedResource.getId();
        log.info("resource created with id: {}", uuid);
        return savedResourceId;
    }

    public Resource getResourceData(String id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        Resource savedResource = resource.orElseThrow(() -> new BadRequestException(String.format(NOT_FOUND_IN_BD, id)));
        log.info("Get resource for id: {}", id);
        return savedResource;
    }

    public List<String> deleteResources(List<String> ids) {
        resourceRepository.deleteAllById(ids);
        log.info("Resources metadata deleted for {} records", ids.size());
        return ids;
    }
}
