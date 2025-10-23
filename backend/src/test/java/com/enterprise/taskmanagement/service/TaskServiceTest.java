package com.enterprise.taskmanagement.service;

import com.enterprise.taskmanagement.dto.TaskCreateRequest;
import com.enterprise.taskmanagement.entity.Task;
import com.enterprise.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private UUID taskId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        testTask = new Task("Test Task", "Test Description", Task.TaskStatus.TODO, Task.TaskPriority.MEDIUM, UUID.randomUUID());
    }

    @Test
    void findById_ShouldReturnTask_WhenTaskExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        Optional<Task> result = taskService.findById(taskId);

        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTitle());
        verify(taskRepository).findById(taskId);
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        TaskCreateRequest request = new TaskCreateRequest("New Task", "Description", Task.TaskStatus.TODO, Task.TaskPriority.HIGH, UUID.randomUUID());
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(request);

        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
        verify(kafkaTemplate).send(eq("task-events"), eq("task.created"), any(Task.class));
    }

    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByStatus(Task.TaskStatus.TODO)).thenReturn(tasks);

        List<Task> result = taskService.findByStatus(Task.TaskStatus.TODO);

        assertEquals(1, result.size());
        verify(taskRepository).findByStatus(Task.TaskStatus.TODO);
    }

    @Test
    void deleteTask_ShouldDeleteTask_WhenTaskExists() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        assertDoesNotThrow(() -> taskService.deleteTask(taskId));

        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(testTask);
        verify(kafkaTemplate).send(eq("task-events"), eq("task.deleted"), eq(taskId.toString()));
    }

    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).delete(any());
    }
}