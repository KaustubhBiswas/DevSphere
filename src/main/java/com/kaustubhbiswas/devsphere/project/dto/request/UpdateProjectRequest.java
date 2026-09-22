package com.kaustubhbiswas.devsphere.project.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UpdateProjectRequest {

    @Size (max = 100, message = "Project name shouldn't exceed 100 characters.")
    private String name;

    @Size (max = 500, message = "Project description shouldn't exceed 500 characters.")
    private String description;
    
}
