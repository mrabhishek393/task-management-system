package com.taskmanager.taskervice.task_manager.service;

import com.taskmanager.taskervice.task_manager.entity.Outbox;
import com.taskmanager.taskervice.task_manager.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxEventPublisherService {

    private static final String TOPIC = "task-events";

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)  // Runs every 5 seconds
    public void publishPendingEvents() {
        // Step 1: Fetch unpublished events from the outbox table
        List<Outbox> unpublishedEvents = outboxRepository.findByPublishedFalse();

        // Step 2: Publish each event to Kafka and mark it as published
        for (Outbox event : unpublishedEvents) {
            try {
                // Publish to Kafka
                kafkaTemplate.send(TOPIC, event.getAggregateId(), event.getPayload());
                System.out.println("Published event: " + event.getPayload());

                // Mark the event as published
                event.setPublished(true);
                outboxRepository.save(event);

            } catch (Exception e) {
                // Log the error and leave the event unpublished for retry
                System.err.println("Error publishing event: " + e.getMessage());
            }
        }
    }
}

