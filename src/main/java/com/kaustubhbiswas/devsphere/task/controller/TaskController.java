package com.kaustubhbiswas.devsphere.task.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaustubhbiswas.devsphere.common.response.ApiResponse;
import com.kaustubhbiswas.devsphere.task.dto.request.CreateTaskRequest;
import com.kaustubhbiswas.devsphere.task.dto.response.TaskResponse;
import com.kaustubhbiswas.devsphere.task.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController 
@RequestMapping ("/api/organizations/{organizationId}/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }
    
    @PostMapping
    public ApiResponse<TaskResponse> createTask(@PathVariable Long organizationId, @PathVariable Long projectId, @Valid @RequestBody CreateTaskRequest request) {
        
        TaskResponse response = taskService.createTask(organizationId, projectId, request);
        
        return ApiResponse.success("Task created successfully.", response);
    }
    
    @GetMapping
    public ApiResponse<List<TaskResponse>> getTasksByProject(@PathVariable Long organizationId, @PathVariable Long projectId) {

        List<TaskResponse> tasks = taskService.getTasksByProject(organizationId, projectId);

        return ApiResponse.success("Tasks fetched successfully.", tasks);
    }
    

}
