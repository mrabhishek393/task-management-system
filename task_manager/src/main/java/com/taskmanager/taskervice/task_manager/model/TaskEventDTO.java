package com.taskmanager.taskervice.task_manager.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskEventDTO {
    private String taskId;
    private String userId;
    private String action; // Created, Updated, Deleted
    private LocalDateTime eventTime;

    public TaskEventDTO(String taskId, String userId, String action, LocalDateTime eventTime) {
        this.taskId = taskId;
        this.userId = userId;
        this.action = action;
        this.eventTime = eventTime;
    }
}

