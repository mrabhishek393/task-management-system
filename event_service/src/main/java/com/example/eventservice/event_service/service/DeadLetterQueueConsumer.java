package com.example.eventservice.event_service.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterQueueConsumer {

    @KafkaListener(topics = "event-service-dlq", groupId = "event-service-group-dlq")
    public void consumeDeadLetterEvent(ConsumerRecord<String, String> record) {
        // Handle DLQ message, log it, or save it for reprocessing
        System.out.println("Failed event moved to DLQ: " + record.key() + " - " + record.value());
    }
}

