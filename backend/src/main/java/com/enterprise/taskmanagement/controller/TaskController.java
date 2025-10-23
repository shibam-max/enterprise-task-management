package com.enterprise.taskmanagement.controller;

import com.enterprise.taskmanagement.dto.TaskCreateRequest;
import com.enterprise.taskmanagement.dto.TaskUpdateRequest;
import com.enterprise.taskmanagement.entity.Task;
import com.enterprise.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name = "Task Management", description = "APIs for managing tasks")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks with optional pagination")
    public ResponseEntity<List<Task>> getAllTasks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.info("Getting all tasks - page: {}, size: {}", page, size);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (page == 0 && size == 20) {
            // Return all tasks for default pagination
            List<Task> tasks = taskService.findAll();
            return ResponseEntity.ok(tasks);
        } else {
            Page<Task> taskPage = taskService.findTasksPaginated(pageable);
            return ResponseEntity.ok(taskPage.getContent());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieve a specific task by its ID")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        logger.info("Getting task by id: {}", id);
        
        return taskService.findById(id)
            .map(task -> ResponseEntity.ok(task))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create new task", description = "Create a new task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskCreateRequest request) {
        logger.info("Creating new task: {}", request.getTitle());
        
        Task createdTask = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update an existing task")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Task> updateTask(@PathVariable UUID id, 
                                          @Valid @RequestBody TaskUpdateRequest request) {
        logger.info("Updating task: {}", id);
        
        try {
            Task updatedTask = taskService.updateTask(id, request);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a task by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        logger.info("Deleting task: {}", id);
        
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve tasks filtered by status")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Task.TaskStatus status) {
        logger.info("Getting tasks by status: {}", status);
        
        List<Task> tasks = taskService.findByStatus(status);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Get tasks by assignee", description = "Retrieve tasks assigned to a specific user")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable UUID assigneeId) {
        logger.info("Getting tasks by assignee: {}", assigneeId);
        
        List<Task> tasks = taskService.findByAssigneeId(assigneeId);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Search tasks by keyword in title or description")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String keyword) {
        logger.info("Searching tasks with keyword: {}", keyword);
        
        List<Task> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/stats/count")
    @Operation(summary = "Get task statistics", description = "Get task count by status")
    public ResponseEntity<TaskStats> getTaskStats() {
        logger.info("Getting task statistics");
        
        TaskStats stats = new TaskStats(
            taskService.countTasksByStatus(Task.TaskStatus.TODO),
            taskService.countTasksByStatus(Task.TaskStatus.IN_PROGRESS),
            taskService.countTasksByStatus(Task.TaskStatus.DONE)
        );
        
        return ResponseEntity.ok(stats);
    }
    
    public static class TaskStats {
        private long todoCount;
        private long inProgressCount;
        private long doneCount;
        
        public TaskStats(long todoCount, long inProgressCount, long doneCount) {
            this.todoCount = todoCount;
            this.inProgressCount = inProgressCount;
            this.doneCount = doneCount;
        }
        
        // Getters
        public long getTodoCount() { return todoCount; }
        public long getInProgressCount() { return inProgressCount; }
        public long getDoneCount() { return doneCount; }
        public long getTotalCount() { return todoCount + inProgressCount + doneCount; }
    }
}