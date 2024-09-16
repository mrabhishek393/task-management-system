package com.example.eventservice.event_service.repository;

import com.example.eventservice.event_service.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventIdentifier(String eventIdentifier);
}

