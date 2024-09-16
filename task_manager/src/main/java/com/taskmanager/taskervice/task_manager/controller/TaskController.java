package com.taskmanager.taskervice.task_manager.controller;

import com.taskmanager.taskervice.task_manager.entity.Task;
import com.taskmanager.taskervice.task_manager.filter.CustomAuthenticationDetails;
import com.taskmanager.taskervice.task_manager.model.TaskCreateDTO;
import com.taskmanager.taskervice.task_manager.model.TaskUpdateDTO;
import com.taskmanager.taskervice.task_manager.service.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    private Long getUserId(Authentication authentication) {
        CustomAuthenticationDetails details = (CustomAuthenticationDetails) authentication.getDetails();
        return details.getUserId();
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO, Authentication authentication) {
        Long userId = getUserId(authentication);
        Task createdTask = taskService.createTask(taskCreateDTO, userId);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        Task updatedTask =taskService.updateTask(id, taskUpdateDTO, userId, isAdmin);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        Task deletedTask =taskService.deleteTask(id, userId, isAdmin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(taskService.getTask(id, userId, isAdmin));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(taskService.getTasks(userId, isAdmin));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskAsComplete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        Task completedTask =taskService.markTaskAsComplete(id, userId, isAdmin);
        return ResponseEntity.ok(completedTask);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(taskService.getTasksByStatus(status, userId, isAdmin));
    }

    @GetMapping("/due-date")
    public ResponseEntity<List<Task>> getTasksByDueDate(@RequestParam String date, Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(taskService.getTasksByDueDate(date, userId, isAdmin));
    }
}