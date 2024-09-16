package com.example.eventservice.event_service.Entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;
    private Long taskId;
    private LocalDateTime timestamp;

    @Column(unique = true)
    private String eventIdentifier; // For idempotency check

}
