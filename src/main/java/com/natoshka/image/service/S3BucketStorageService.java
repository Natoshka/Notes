package com.natoshka.image.service;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.web.multipart.MultipartFile;

public interface S3BucketStorageService {

    String uploadFile(String fileName, MultipartFile file);

    S3ObjectInputStream downloadFile(String fileName);
}
