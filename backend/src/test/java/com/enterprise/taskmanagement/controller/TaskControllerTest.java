package com.enterprise.taskmanagement.controller;

import com.enterprise.taskmanagement.entity.Task;
import com.enterprise.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        Task task = new Task("Test Task", "Description", Task.TaskStatus.TODO, Task.TaskPriority.MEDIUM, UUID.randomUUID());
        when(taskService.findAll()).thenReturn(Arrays.asList(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    @WithMockUser
    void getTaskById_ShouldReturnTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test Task", "Description", Task.TaskStatus.TODO, Task.TaskPriority.MEDIUM, UUID.randomUUID());
        when(taskService.findById(taskId)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTask_ShouldReturnCreatedTask() throws Exception {
        Task task = new Task("New Task", "Description", Task.TaskStatus.TODO, Task.TaskPriority.HIGH, UUID.randomUUID());
        when(taskService.createTask(any())).thenReturn(task);

        String taskJson = """
            {
                "title": "New Task",
                "description": "Description",
                "status": "TODO",
                "priority": "HIGH"
            }
            """;

        mockMvc.perform(post("/api/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }
}