package com.taskmanager.taskervice.task_manager.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskUpdateDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private String priority;

}