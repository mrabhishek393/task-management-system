package com.taskmanager.taskervice.task_manager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Data
public class Outbox{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aggregate_id")
    private String aggregateId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published")
    private boolean published = false;

    public Outbox() {}

    public Outbox(String aggregateId, String eventType, String payload, LocalDateTime createdAt) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = createdAt;
        this.published = false; // Default to unpublished
    }

}
