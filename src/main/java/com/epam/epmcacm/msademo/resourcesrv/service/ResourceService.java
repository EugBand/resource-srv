package com.epam.epmcacm.msademo.resourcesrv.service;

import com.epam.epmcacm.msademo.resourcesrv.entity.Resource;
import com.epam.epmcacm.msademo.resourcesrv.exception.BadRequestException;
import com.epam.epmcacm.msademo.resourcesrv.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.amazonaws.services.mediaconvert.model.AudioCodec.MP3;

@Service
@Slf4j
public class ResourceService {

    public static final String ERROR_UPLOADING_MP3_FILE = "Error uploading mp3 file: ";

    public static final String ERROR_DOWNLOADING_MP_3_FILE = "Error downloading mp3 file: ";

    public static final String ERROR_RETRIEVINGMP_3_FILE_FOR_ID = "Error retrieving mp3 file for id:";

    public static final String MISMATCH_COUNT = "Mismatch count of resources: %s and mp3 deleted files:";

    public static final String IS_NOT_OF_MP_3_FORMAT = "Provided file is not of mp3 format";

    @Autowired H2DBService dbService;

    @Autowired AwsS3Service s3FileService;

    @Autowired RMQPublisherService publisher;

    public String createResource(MultipartFile multipartFile, String fileName) {
        if(!validateIfMp3File(multipartFile)) {
            throw new BadRequestException(IS_NOT_OF_MP_3_FORMAT);
        }
        String id = UUID.randomUUID().toString();
        dbService.addResourceData(id, fileName);
        String resourceId;
        try {
            resourceId = s3FileService.upLoadMp3(multipartFile, id);
        } catch (IOException e) {
            log.error(ERROR_UPLOADING_MP3_FILE + e.getMessage());
            deleteResources(List.of(id));
            throw new FileProcessingException(ERROR_UPLOADING_MP3_FILE + e.getMessage(), e);
        }
        publisher.publishCreationEvent(resourceId);
        log.info("resource created with path: {}", resourceId);
        return resourceId;
    }

    public Resource getResource(String id) {
        Resource resource = dbService.getResourceData(id);
            try {
               resource.setMp3data(s3FileService.downLoadMp3(resource.getId()));
            } catch (IOException e) {
                log.error(ERROR_DOWNLOADING_MP_3_FILE + e.getMessage());
                throw new FileProcessingException(ERROR_DOWNLOADING_MP_3_FILE + e.getMessage(), e);
            };
        log.info("Get resource for id: {}", id);
        return resource;
    }

    public List<String> deleteResources(List<String> ids) {
        log.info("Resources metadata deleted for {} records", ids.size());
        List<String> deletedDbResources = dbService.deleteResources(ids);
        List<String> deletedS3Resources = ids.stream().map(id -> s3FileService.deleteMp3(id)).collect(Collectors.toList());
        if (deletedDbResources != deletedS3Resources) {
            throw new BadRequestException(
                    String.format(MISMATCH_COUNT + " %s", deletedS3Resources.size(),
                            deletedDbResources.size()));
        }
        return deletedS3Resources;
    }

    private boolean validateIfMp3File(final MultipartFile file) {
        return MP3.toString().equalsIgnoreCase(FilenameUtils.getExtension(file.getResource().getFilename()));
    }
}
