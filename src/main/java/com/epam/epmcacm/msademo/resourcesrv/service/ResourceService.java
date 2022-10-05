package com.epam.epmcacm.msademo.resourcesrv.service;

import com.epam.epmcacm.msademo.resourcesrv.entity.Resource;
import com.epam.epmcacm.msademo.resourcesrv.exception.BadRequestException;
import com.epam.epmcacm.msademo.resourcesrv.exception.FileProcessingException;
import com.epam.epmcacm.msademo.resourcesrv.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceService {

    public static final String ERROR_PARSING_MP3_FILE = "Error parsing mp3 file: ";

    public static final String ERROR_DTO_MAPPING = "Error DTO mapping";

    public static final String ERROR_UPLOADING_MP3_FILE = "Error uploading mp3 file: ";

    public static final String ERROR_DOWNLOADING_MP_3_FILE = "Error downloading mp3 file: ";

    public static final String ERROR_RETRIEVINGMP_3_FILE_FOR_ID = "Error retrieving mp3 file for id:";

    public static final String MISMATCH_COUNT = "Mismatch count of resources: %s and mp3 deleted files:";

    @Autowired ResourceRepository resourceRepository;

    @Autowired AwsS3Service s3FileService;

    public String createResource(MultipartFile multipartFile, String name) {
        Optional<Resource> resourceDto;
        String id = UUID.randomUUID().toString();
        try {
            resourceDto = Optional.ofNullable(Resource.builder()
                    .id(id)
                    .fileName(name)
                    .createdAt(Instant.now())
                    .mp3data(multipartFile.getBytes())
                    .build());
        } catch (IOException e) {
            log.error(ERROR_PARSING_MP3_FILE + e.getMessage());
            throw new BadRequestException(ERROR_PARSING_MP3_FILE, e);
        }
        resourceDto.ifPresentOrElse(dto -> resourceRepository.save(dto), () -> {
            throw new BadRequestException(ERROR_DTO_MAPPING);
        });

        try {
            s3FileService.upLoadMp3(multipartFile, id);
        } catch (IOException e) {
            log.error(ERROR_UPLOADING_MP3_FILE + e.getMessage());
            deleteResources(List.of(id));
            throw new FileProcessingException(ERROR_UPLOADING_MP3_FILE + e.getMessage(), e);
        }
        log.info("resource created with id: {}", resourceDto.get().getId());
        return resourceDto.get().getId();
    }

    public Resource getResource(String id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        resource.ifPresentOrElse(r -> {
            try {
                s3FileService.downLoadMp3(r.getId());
            } catch (IOException e) {
                log.error(ERROR_DOWNLOADING_MP_3_FILE + e.getMessage());
                throw new FileProcessingException(ERROR_DOWNLOADING_MP_3_FILE + e.getMessage(), e);
            }
        }, () -> {
            throw new BadRequestException(String.format(ERROR_RETRIEVINGMP_3_FILE_FOR_ID + " %s", id));
        });
        log.info("Get resource for id: {}", id);
        return resource.get();
    }

    public List<String> deleteResources(List<String> ids) {
        resourceRepository.deleteAllById(ids);
        log.info("Resources metadata deleted for {} records", ids.size());
        List<String> deletedIds = ids.stream().map(id -> s3FileService.deleteMp3(id)).collect(Collectors.toList());
        if (ids.size() != deletedIds.size()) {
            throw new BadRequestException(
                    String.format(MISMATCH_COUNT + " %s", ids.size(),
                            deletedIds.size()));
        }
        return ids;
    }
}
