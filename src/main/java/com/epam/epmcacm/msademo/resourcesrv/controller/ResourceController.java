package com.epam.epmcacm.msademo.resourcesrv.controller;

import com.epam.epmcacm.msademo.resourcesrv.entity.Resource;
import com.epam.epmcacm.msademo.resourcesrv.service.ResourceService;
import com.epam.epmcacm.msademo.resourcesrv.validation.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/resources", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceController {

    @Autowired ResourceService service;

    @PostMapping
    public String addResource(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) throws IOException {
        return service.createResource(file, name);
    }


    @GetMapping("{id}")
    public Resource getResource(@PathVariable @UUID String id) {
        return service.getResource(id);
    }

    @DeleteMapping
    public List<String> deleteResource(@RequestParam List<String> ids) {
        return service.deleteResources(ids);
    }
}
