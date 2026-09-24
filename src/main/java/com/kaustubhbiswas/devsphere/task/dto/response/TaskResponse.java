package com.kaustubhbiswas.devsphere.task.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kaustubhbiswas.devsphere.task.TaskPriority;
import com.kaustubhbiswas.devsphere.task.TaskStatus;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private Long projectId;
    private Long createdById;
    private Long assignedToId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
