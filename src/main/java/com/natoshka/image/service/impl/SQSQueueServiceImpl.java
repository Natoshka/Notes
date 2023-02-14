package com.natoshka.image.service.impl;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natoshka.image.dto.ImageToProcessDto;
import com.natoshka.image.exception.AWSException;
import com.natoshka.image.service.SQSQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSQueueServiceImpl implements SQSQueueService {

    @Value("${cloud.aws.sqs.queue.url}")
    private String queueUrl;

    private final AmazonSQSAsync amazonSQSClient;
    private final ObjectMapper objectMapper;

    @Override
    public void sendToQueue(String uniqFileName) {
        try {
            SendMessageRequest messageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(
                    objectMapper.writeValueAsString(
                        ImageToProcessDto.builder()
                            .fileName(uniqFileName)
                            .build()));
            amazonSQSClient.sendMessage(messageRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AWSException(e.getMessage());
        }
    }

    @Override
    public List<Message> receiveFromQueue() {
        return amazonSQSClient.receiveMessage(queueUrl).getMessages();
    }

    @Override
    public void deleteProceededMessage(Message message) {
        amazonSQSClient.deleteMessage(queueUrl, message.getReceiptHandle());
    }

}
