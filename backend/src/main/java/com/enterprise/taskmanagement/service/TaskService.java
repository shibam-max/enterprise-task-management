package com.enterprise.taskmanagement.service;

import com.enterprise.taskmanagement.dto.TaskCreateRequest;
import com.enterprise.taskmanagement.dto.TaskUpdateRequest;
import com.enterprise.taskmanagement.entity.Task;
import com.enterprise.taskmanagement.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Cacheable(value = "tasks", key = "#id")
    public Optional<Task> findById(UUID id) {
        logger.debug("Finding task by id: {}", id);
        return taskRepository.findById(id);
    }
    
    @Cacheable(value = "allTasks")
    public List<Task> findAll() {
        logger.debug("Finding all tasks");
        return taskRepository.findAll();
    }
    
    public List<Task> findByStatus(Task.TaskStatus status) {
        logger.debug("Finding tasks by status: {}", status);
        return taskRepository.findByStatus(status);
    }
    
    public List<Task> findByAssigneeId(UUID assigneeId) {
        logger.debug("Finding tasks by assignee: {}", assigneeId);
        return taskRepository.findByAssigneeId(assigneeId);
    }
    
    @CacheEvict(value = {"tasks", "allTasks"}, allEntries = true)
    public Task createTask(TaskCreateRequest request) {
        logger.info("Creating new task: {}", request.getTitle());
        
        Task task = new Task(
            request.getTitle(),
            request.getDescription(),
            request.getStatus() != null ? request.getStatus() : Task.TaskStatus.TODO,
            request.getPriority() != null ? request.getPriority() : Task.TaskPriority.MEDIUM,
            request.getAssigneeId()
        );
        
        Task savedTask = taskRepository.save(task);
        
        // Publish task created event
        kafkaTemplate.send("task-events", "task.created", savedTask);
        logger.info("Task created successfully with id: {}", savedTask.getId());
        
        return savedTask;
    }
    
    @CacheEvict(value = {"tasks", "allTasks"}, allEntries = true)
    public Task updateTask(UUID id, TaskUpdateRequest request) {
        logger.info("Updating task: {}", id);
        
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getAssigneeId() != null) {
            task.setAssigneeId(request.getAssigneeId());
        }
        
        Task updatedTask = taskRepository.save(task);
        
        // Publish task updated event
        kafkaTemplate.send("task-events", "task.updated", updatedTask);
        logger.info("Task updated successfully: {}", id);
        
        return updatedTask;
    }
    
    @CacheEvict(value = {"tasks", "allTasks"}, allEntries = true)
    public void deleteTask(UUID id) {
        logger.info("Deleting task: {}", id);
        
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        taskRepository.delete(task);
        
        // Publish task deleted event
        kafkaTemplate.send("task-events", "task.deleted", id.toString());
        logger.info("Task deleted successfully: {}", id);
    }
    
    public Page<Task> findTasksPaginated(Pageable pageable) {
        logger.debug("Finding tasks with pagination: {}", pageable);
        return taskRepository.findAll(pageable);
    }
    
    public List<Task> searchTasks(String keyword) {
        logger.debug("Searching tasks with keyword: {}", keyword);
        return taskRepository.searchByKeyword(keyword);
    }
    
    public long countTasksByStatus(Task.TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
}