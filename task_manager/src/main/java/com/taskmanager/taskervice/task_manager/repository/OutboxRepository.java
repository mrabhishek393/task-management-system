package com.taskmanager.taskervice.task_manager.repository;

import com.taskmanager.taskervice.task_manager.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    // Fetch all unpublished events
    List<Outbox> findByPublishedFalse();
}
