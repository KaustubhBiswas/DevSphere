package com.kaustubhbiswas.devsphere.task.dto.request;

import java.time.LocalDate;

import com.kaustubhbiswas.devsphere.task.TaskPriority;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required.")
    @Size (max = 100, message = "Task title shouldn't exceed 100 characters.")
    private String title;

    @Size(max = 500, message = "Task description shouldn't exceed 500 characters.")
    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;

    private Long assignedTo;
}
