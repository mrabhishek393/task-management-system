package com.taskmanager.taskervice.task_manager.repository;

import com.taskmanager.taskervice.task_manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}