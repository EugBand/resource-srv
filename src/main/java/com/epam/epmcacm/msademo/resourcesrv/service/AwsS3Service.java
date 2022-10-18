package com.epam.epmcacm.msademo.resourcesrv.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.epmcacm.msademo.resourcesrv.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

@Service
public class AwsS3Service {

    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    private final AmazonS3 amazonS3;

    @Autowired
    public AwsS3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${cloud.aws.s3-bucket-name}")
    private String s3BucketName;

    @Value("${cloud.aws.s3-mp3-folder}")
    private String s3Mp3Folder;

    private static final String fileExtension = ".mp3";

    public String upLoadMp3(MultipartFile mp3File, String resourceId) throws IOException {
        String path = getFilePath(resourceId);
        File file = multipartToFile(mp3File, resourceId + fileExtension);
        amazonS3.putObject(s3BucketName, path, file);
        return resourceId;
    }

    public byte[] downLoadMp3(String resourceId) throws IOException {
        String mp3FilePath = getFilePath(resourceId);
        ObjectListing objectListing = amazonS3.listObjects(s3BucketName);
        if (objectListing.getObjectSummaries().stream().anyMatch(item -> item.getKey().equals(mp3FilePath))) {
            S3Object s3object = amazonS3.getObject(s3BucketName, mp3FilePath);
            return s3object.getObjectContent().getDelegateStream().readAllBytes();
        }
        throw new BadRequestException(String.format("File with id: %s in S3 repo not dound", resourceId));
    }

    public String deleteMp3(String resourceId){
        amazonS3.deleteObject(s3BucketName, getFilePath(resourceId));
        return resourceId + fileExtension;
    }

    private String getFilePath(String resourceId){
        return s3Mp3Folder + File.separator + resourceId + fileExtension;
    }

    private String getFullFilePath(String resourceId, String buckedName){
        return buckedName + File.separator + s3Mp3Folder + File.separator + resourceId + fileExtension;
    }

    @NotNull
    private File multipartToFile(@NotNull MultipartFile multipart, String fileName)
            throws IOException {
        File convFile = new File(System.getProperty(JAVA_IO_TMPDIR) + File.separator + fileName);
        multipart.transferTo(convFile);
        return convFile;
    }
}
