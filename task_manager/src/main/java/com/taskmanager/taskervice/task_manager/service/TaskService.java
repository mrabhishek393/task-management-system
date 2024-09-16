package com.taskmanager.taskervice.task_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.taskervice.task_manager.entity.Outbox;
import com.taskmanager.taskervice.task_manager.entity.Task;
import com.taskmanager.taskervice.task_manager.entity.TaskStatus;
import com.taskmanager.taskervice.task_manager.model.TaskEventDTO;
import com.taskmanager.taskervice.task_manager.exception.AccessDeniedException;
import com.taskmanager.taskervice.task_manager.exception.ResourceNotFoundException;
import com.taskmanager.taskervice.task_manager.model.TaskCreateDTO;
import com.taskmanager.taskervice.task_manager.model.TaskUpdateDTO;
import com.taskmanager.taskervice.task_manager.repository.OutboxRepository;
import com.taskmanager.taskervice.task_manager.repository.TaskRepository;
import com.taskmanager.taskervice.task_manager.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Outbox createOutboxEvent(Task task, Long userId, String action) {

        TaskEventDTO taskEvent = new TaskEventDTO(
                task.getId().toString(),
                userId.toString(),
                action,
                LocalDateTime.now()
        );
        String eventPayload;
        try {
            eventPayload = objectMapper.writeValueAsString(taskEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing task event", e);
        }
        Outbox outboxEvent = new Outbox(
                task.getId().toString(),
                action,
                eventPayload,
                LocalDateTime.now()
        );
        return outboxEvent;
    }
    @Transactional
    public Task createTask(TaskCreateDTO taskCreateDTO, Long userId) {
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setDescription(taskCreateDTO.getDescription());
        task.setDueDate(taskCreateDTO.getDueDate());
        task.setUserId(userId);

        // Create the task status
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setStatusName("PENDING");
        taskStatus.setPriority("Medium");

        task.setStatus(taskStatus);

        // Associate the task with the status and the status with the task
        taskStatus.setTask(task);  // Set the reverse relationship

        task = taskRepository.save(task);

        Outbox outboxEvent = createOutboxEvent(task, userId,"created");

        outboxRepository.save(outboxEvent);

        // Save only the task (TaskStatus will be saved due to CascadeType.ALL)
        return task;

    }

    @Transactional
    public Task updateTask(Long id, TaskUpdateDTO taskUpdateDTO, Long userId, boolean isAdmin) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAdmin && !task.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }

        if (taskUpdateDTO.getTitle() != null) task.setTitle(taskUpdateDTO.getTitle());
        if (taskUpdateDTO.getDescription() != null) task.setDescription(taskUpdateDTO.getDescription());
        if (taskUpdateDTO.getDueDate() != null) task.setDueDate(taskUpdateDTO.getDueDate());

        task.setUpdatedAt(LocalDateTime.now());

        if (taskUpdateDTO.getStatus() != null || taskUpdateDTO.getPriority() != null) {
            TaskStatus status = task.getStatus();
            System.out.println("update task");
            System.out.println(status);
            if (status == null) {
                status = new TaskStatus();
                task.setStatus(status);
            }
            if (taskUpdateDTO.getStatus() != null) status.setStatusName(taskUpdateDTO.getStatus());
            if (taskUpdateDTO.getPriority() != null) status.setPriority(taskUpdateDTO.getPriority());
        }
        System.out.println("out of update task");
        Task updatedTask = taskRepository.save(task);

        Outbox outboxEvent = createOutboxEvent(updatedTask, userId,"updated");
        outboxRepository.save(outboxEvent);
        return updatedTask;
    }

    @Transactional
    public Task deleteTask(Long id, Long userId, boolean isAdmin) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAdmin && !task.getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
        Outbox outboxEvent = createOutboxEvent(task, userId,"deleted");
        outboxRepository.save(outboxEvent);
        return task;
    }

    public Task getTask(Long id, Long userId, boolean isAdmin) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!isAdmin && !task.getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to view this task");
        }

        return task;
    }

    public List<Task> getTasks(Long userId, boolean isAdmin) {
        if (isAdmin) {
            return taskRepository.findAll();
        } else {
            return taskRepository.findByUserId(userId);
        }
    }

    @Transactional
    public Task markTaskAsComplete(Long id, Long userId, boolean isAdmin) {
        Task task = getTask(id, userId, isAdmin); // This will handle permission checks
        if (!isAdmin && !task.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to update this task");
        }
        TaskStatus completedStatus = task.getStatus();
        if (completedStatus == null) {
            completedStatus = new TaskStatus();
        }
        completedStatus.setStatusName("COMPLETED");
        completedStatus.setPriority(task.getStatus().getPriority());

        Task completedTask = taskRepository.save(task);

        Outbox outboxEvent = createOutboxEvent(completedTask, userId,"completed");
        outboxRepository.save(outboxEvent);

        return completedTask;
    }

    public List<Task> getTasksByStatus(String status, Long userId, boolean isAdmin) {
        List<Task> tasks;
        if (isAdmin) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByUserId(userId);
        }

        return tasks.stream()
                .filter(task -> task.getStatus().getStatusName().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByDueDate(String date, Long userId, boolean isAdmin) {
        LocalDate dueDate = LocalDate.parse(date);
        List<Task> tasks;
        if (isAdmin) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByUserId(userId);
        }

        return tasks.stream()
                .filter(task -> task.getDueDate().equals(dueDate))
                .collect(Collectors.toList());
    }

}