package com.natoshka.image.service;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface SQSQueueService {

    void sendToQueue(String uniqFileName);

    List<Message> receiveFromQueue();

    void deleteProceededMessage(Message message);
}
