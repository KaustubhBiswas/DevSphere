package com.kaustubhbiswas.devsphere.project.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;

    private Long organizationId;
    private String organizationName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
