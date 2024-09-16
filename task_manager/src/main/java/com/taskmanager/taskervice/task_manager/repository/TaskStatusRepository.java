package com.taskmanager.taskervice.task_manager.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taskmanager.taskervice.task_manager.entity.TaskStatus;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    // You can add custom queries if needed, but JpaRepository gives basic CRUD methods out of the box.
}
