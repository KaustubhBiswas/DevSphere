package com.kaustubhbiswas.devsphere.project.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kaustubhbiswas.devsphere.common.exception.BusinessValidationException;
import com.kaustubhbiswas.devsphere.common.exception.ResourceNotFoundException;
import com.kaustubhbiswas.devsphere.organization.Organization;
import com.kaustubhbiswas.devsphere.organization.repository.OrganizationMemberRepository;
import com.kaustubhbiswas.devsphere.organization.repository.OrganizationRepository;
import com.kaustubhbiswas.devsphere.project.Project;
import com.kaustubhbiswas.devsphere.project.dto.request.CreateProjectRequest;
import com.kaustubhbiswas.devsphere.project.dto.response.ProjectResponse;
import com.kaustubhbiswas.devsphere.project.repository.ProjectRepository;
import com.kaustubhbiswas.devsphere.user.User;
import com.kaustubhbiswas.devsphere.user.UserRepository;

@Service 
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, OrganizationRepository organizationRepository, OrganizationMemberRepository organizationMemberRepository, UserRepository userRepository){
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.userRepository = userRepository;
    }

    private ProjectResponse toResponse(Project project){

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());

        response.setOrganizationId(project.getOrganization().getId());
        response.setOrganizationName(project.getOrganization().getName());

        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        return response;

    }

    public ProjectResponse createProject(Long organizationId, CreateProjectRequest request){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User not found with email: " + email));
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + organizationId));
        organizationMemberRepository.findByOrganizationIdAndUserId(organizationId, requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));
        
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOrganization(organization);

        Project savedProject = projectRepository.save(project);

        return toResponse(savedProject);
    }

    public ProjectResponse getProjectById(Long projectId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        organizationMemberRepository.findByOrganizationIdAndUserId(project.getOrganization().getId(), requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        return toResponse(project);

    }

    public List<ProjectResponse> getProjectsByOrganizationId(Long organizationId){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        organizationRepository.findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + organizationId));
        organizationMemberRepository.findByOrganizationIdAndUserId(organizationId, requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        return projectRepository.findByOrganizationId(organizationId).stream().map(this::toResponse).toList();

    }

}
