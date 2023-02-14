package com.natoshka.image.service.impl;

import com.natoshka.image.entity.Image;
import com.natoshka.image.repository.ImageRepository;
import com.natoshka.image.service.ImageService;
import com.natoshka.image.service.S3BucketStorageService;
import com.natoshka.image.service.SQSQueueService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3BucketStorageService s3BucketStorageService;
    private final SQSQueueService sqsQueueService;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public String upload(MultipartFile image) {
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        String uniqFileName = UUID.randomUUID() + "." + extension;
        Image imageEntity = Image.builder()
            .name(image.getOriginalFilename())
            .size(image.getSize())
            .uploadAt(LocalDateTime.now())
            .path(uniqFileName)
            .contentType(image.getContentType())
            .build();

        imageRepository.save(imageEntity);
        String result = s3BucketStorageService.uploadFile(uniqFileName, image);
        sqsQueueService.sendToQueue(uniqFileName);

        return result;
    }

    @Override
    public Image getMetadata(String fileName) {
        return imageRepository.getByPath(fileName);
    }


}
