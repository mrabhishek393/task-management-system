package com.taskmanager.taskervice.task_manager.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.taskervice.task_manager.model.TaskEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskEventPublisherService {

    private static final String TOPIC = "task-events";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper for serializing the event to JSON

    public void publishTaskEvent(String taskId, String userId ,String eventType) {



        // Create a TaskEventDTO with the necessary information
        TaskEventDTO taskEvent = new TaskEventDTO(taskId, userId, eventType, LocalDateTime.now());

        try {
            // Serialize TaskEventDTO to JSON
            String message = objectMapper.writeValueAsString(taskEvent);

            // Publish the event to Kafka
            kafkaTemplate.send(TOPIC, taskId, message);
            System.out.println("Sent task event: " + message);

        } catch (JsonProcessingException e) {
            System.err.println("Error serializing task event: " + e.getMessage());
            // Handle error (logging, etc.)
        }
    }
}
