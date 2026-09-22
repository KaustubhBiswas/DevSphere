package com.kaustubhbiswas.devsphere.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaustubhbiswas.devsphere.common.response.ApiResponse;
import com.kaustubhbiswas.devsphere.project.dto.request.CreateProjectRequest;
import com.kaustubhbiswas.devsphere.project.dto.request.UpdateProjectRequest;
import com.kaustubhbiswas.devsphere.project.dto.response.ProjectResponse;
import com.kaustubhbiswas.devsphere.project.service.ProjectService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;



@RestController 
@RequestMapping ("/api/organizations/{organizationId}/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService){
        this.projectService = projectService;
    }

    @PostMapping
    public ApiResponse<ProjectResponse> createProject(@PathVariable Long organizationId, @Valid @RequestBody CreateProjectRequest request) {
        
        ProjectResponse response = projectService.createProject(organizationId, request);
        
        return ApiResponse.success("Project created successfully.", response);
    }

    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> getProjectById(@PathVariable Long projectId) {

        ProjectResponse response = projectService.getProjectById(projectId);

        return ApiResponse.success("Project fetched successfully.", response);
    }
    
    @GetMapping
    public ApiResponse<List<ProjectResponse>> getProjectsByOrganizationId(@PathVariable Long organizationId) {

        List<ProjectResponse> response = projectService.getProjectsByOrganizationId(organizationId);

        return ApiResponse.success("Projects fetched successfully.", response);
    }

    @PutMapping("/{projectId}")
    public ApiResponse<ProjectResponse> updateProject(@PathVariable Long projectId, @PathVariable Long organizationId, @Valid @RequestBody UpdateProjectRequest request) {
        
        ProjectResponse response = projectService.updateProject(projectId, organizationId, request);
        
        return ApiResponse.success("Project updated successfully.", response);
    }
    
}
