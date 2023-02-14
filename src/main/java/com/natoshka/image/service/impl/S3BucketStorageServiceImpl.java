package com.natoshka.image.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.natoshka.image.exception.AWSException;
import com.natoshka.image.service.S3BucketStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3BucketStorageServiceImpl implements S3BucketStorageService {

    @Value("${cloud.aws.s3.bucket.name.origin}")
    private String bucketName;

    private final AmazonS3 amazonS3Client;

    @Override
    public String uploadFile(String fileName, MultipartFile file) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
            return fileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AWSException(e.getMessage());
        }
    }

    @Override
    public S3ObjectInputStream downloadFile(String fileName) {
        S3Object s3object = amazonS3Client.getObject(bucketName, fileName);
        return s3object.getObjectContent();
    }

}
