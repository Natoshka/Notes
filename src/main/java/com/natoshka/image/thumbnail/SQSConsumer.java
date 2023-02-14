package com.natoshka.image.thumbnail;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natoshka.image.dto.ImageToProcessDto;
import com.natoshka.image.exception.AWSException;
import com.natoshka.image.service.ImageService;
import com.natoshka.image.service.S3BucketStorageService;
import com.natoshka.image.service.SQSQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSConsumer {

    private final ObjectMapper objectMapper;
    private final S3BucketStorageService s3BucketStorageService;
    private final SQSQueueService sqsQueueService;
    private final ImageService imageService;

    @Scheduled(fixedDelay = 3000)
    public void receive() {
        List<Message> messages = sqsQueueService.receiveFromQueue();
        for (Message message : messages) {
            try {
                ImageToProcessDto imageToProcessDto = objectMapper.readValue(
                    message.getBody().getBytes(StandardCharsets.UTF_8), ImageToProcessDto.class);
                createSmallImage(imageToProcessDto.getFileName());
                sqsQueueService.deleteProceededMessage(message);
                log.info("Successful proceed the file " + imageToProcessDto.getFileName());

            } catch (IOException e) {
                log.error("Cannot parse JSON {}", e.getMessage());
            }
        }
    }

    private void createSmallImage(String fileName) {
        S3ObjectInputStream s3ObjectInputStream = s3BucketStorageService.downloadFile(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        String newFile = fileName.substring(0, fileName.length() - extension.length() - 1) + "_thumbnail." + extension;
        com.natoshka.image.entity.Image metadata = imageService.getMetadata(fileName);
        try {
            BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            img.createGraphics()
                .drawImage(ImageIO.read(s3ObjectInputStream)
                    .getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, extension, os);
            MultipartFile newMultipart = new MockMultipartFile(newFile, newFile,
                metadata.getContentType(), new ByteArrayInputStream(os.toByteArray()));

            s3BucketStorageService.uploadFile(newFile, newMultipart);
        } catch (IOException e) {
            throw new AWSException("Creation thumbnail is failed", e);
        }
    }
}
