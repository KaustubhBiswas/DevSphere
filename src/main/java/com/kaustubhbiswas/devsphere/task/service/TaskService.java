package com.kaustubhbiswas.devsphere.task.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kaustubhbiswas.devsphere.common.exception.BusinessValidationException;
import com.kaustubhbiswas.devsphere.common.exception.ResourceNotFoundException;
import com.kaustubhbiswas.devsphere.organization.repository.OrganizationMemberRepository;
import com.kaustubhbiswas.devsphere.project.Project;
import com.kaustubhbiswas.devsphere.project.repository.ProjectRepository;
import com.kaustubhbiswas.devsphere.task.Task;
import com.kaustubhbiswas.devsphere.task.TaskPriority;
import com.kaustubhbiswas.devsphere.task.TaskStatus;
import com.kaustubhbiswas.devsphere.task.dto.request.CreateTaskRequest;
import com.kaustubhbiswas.devsphere.task.dto.response.TaskResponse;
import com.kaustubhbiswas.devsphere.task.repository.TaskRepository;
import com.kaustubhbiswas.devsphere.user.User;
import com.kaustubhbiswas.devsphere.user.UserRepository;

@Service 
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, OrganizationMemberRepository organizationMemberRepository, UserRepository userRepository){
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userRepository = userRepository;
    }

    private TaskResponse toResponse(Task task){

        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setProjectId(task.getProject().getId());
        response.setCreatedById(task.getCreatedBy().getId());
        response.setAssignedToId(task.getAssignedTo()!=null ? task.getAssignedTo().getId() : null);
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;

    }

    public TaskResponse createTask(Long organizationId,Long projectId, CreateTaskRequest request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOrganization().getId().equals(organizationId)){
            throw new BusinessValidationException("Project doesn't belong to this organization.");
        }

        organizationMemberRepository.findByOrganizationIdAndUserId(project.getOrganization().getId(), requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        User assignedTo = null;

        if (request.getAssignedTo()!=null){
            assignedTo = userRepository.findById(request.getAssignedTo()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedTo()));
            organizationMemberRepository.findByOrganizationIdAndUserId(project.getOrganization().getId(), request.getAssignedTo()).orElseThrow(() -> new BusinessValidationException("Assigned user is not a member of this organization."));
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority()!=null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());
        task.setProject(project);
        task.setCreatedBy(requester);
        task.setAssignedTo(assignedTo);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
        
    }

    public List<TaskResponse> getTasksByProject(Long organizationId, Long projectId){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found."));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (!project.getOrganization().getId().equals(organizationId)){
            throw new BusinessValidationException("Project doesn't belong to this organization.");
        }

        organizationMemberRepository.findByOrganizationIdAndUserId(organizationId, requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        return taskRepository.findByProjectId(projectId).stream().map(this::toResponse).toList();

    }

}
