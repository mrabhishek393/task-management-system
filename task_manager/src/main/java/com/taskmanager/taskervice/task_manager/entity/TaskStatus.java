package com.taskmanager.taskervice.task_manager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_statuses")
public class TaskStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;

    @Column(nullable = false, length = 50)
    private String priority;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonBackReference
    private Task task;

    // Getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}