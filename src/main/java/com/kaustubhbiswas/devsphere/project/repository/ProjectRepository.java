package com.kaustubhbiswas.devsphere.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaustubhbiswas.devsphere.project.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{

    List<Project> findByOrganizationId(Long organizationId);
    
}
