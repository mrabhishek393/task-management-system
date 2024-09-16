package com.example.eventservice.event_service.service;

import com.example.eventservice.event_service.Entity.Outbox;
import com.example.eventservice.event_service.model.TaskEventDTO;
import com.example.eventservice.event_service.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EventConsumerService {

    private final EventRepository eventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.dead-letter}")
    private String deadLetterTopic;

    @Value("${kafka.topic.notification}")
    private String notificationTopic;

    @Autowired
    private ObjectMapper objectMapper;  // For JSON deserialization

    public EventConsumerService(EventRepository eventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${kafka.topic.task-events}", groupId = "${kafka.group-id}")
    @Transactional
    public void consumeTaskEvent(ConsumerRecord<String, String> record) {
        try {
            // Step 1: Deserialize the outer Kafka message (Outbox structure)
            Outbox outboxEvent = objectMapper.readValue(record.value(), Outbox.class);

            // Step 2: Extract the payload and deserialize it into TaskEventDTO
            TaskEventDTO taskEvent = objectMapper.readValue(outboxEvent.getPayload(), TaskEventDTO.class);

            // Step 3: Process the actual task event
            sendNotification(taskEvent);

        } catch (JsonProcessingException e) {
            // Handle JSON parsing exceptions
            System.err.println("Error deserializing task event: " + e.getMessage());
        } catch (Exception e) {
            // Handle other processing exceptions
            System.err.println("Error processing task event: " + e.getMessage());
        }
    }

    private void sendNotification(TaskEventDTO event) {
        // Construct the notification message using the task ID, action, and user ID
        String notificationMessage = String.format(
                "Task ID: %s, Action: %s, User ID: %s, Event Time: %s",
                event.getTaskId(),
                event.getAction(),
                event.getUserId(),
                event.getEventTime()
        );

        try {
            // Send the notification to the Kafka topic for notifications
            kafkaTemplate.send(notificationTopic, String.valueOf(event.getTaskId()), notificationMessage);
            System.out.println("Sent notification: " + notificationMessage);

        } catch (Exception e) {
            // Handle the case where sending notification to Kafka fails
            System.err.println("Failed to send notification for event: " + event.getTaskId());

            // Optionally, send the failed message to a Dead Letter Queue (DLQ)
            kafkaTemplate.send(deadLetterTopic, String.valueOf(event.getTaskId()), notificationMessage);
            throw new RuntimeException("Failed to send notification, moved to DLQ", e);
        }
    }
}

