package com.taskmanager.taskervice.task_manager.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskCreateDTO {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

}