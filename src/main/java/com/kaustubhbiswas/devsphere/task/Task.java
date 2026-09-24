package com.kaustubhbiswas.devsphere.task;


import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.kaustubhbiswas.devsphere.project.Project;
import com.kaustubhbiswas.devsphere.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@Entity 
@EntityListeners(AuditingEntityListener.class)
@Table (name = "tasks")
public class Task {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String title;

    @Column (length = 500)
    private String description;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;

    private LocalDate dueDate;

    @CreatedDate 
    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate 
    @Column (nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne 
    @JoinColumn (name = "project_id", nullable = false)
    private Project project;

    @ManyToOne 
    @JoinColumn (name = "created_by_id", nullable = false)
    private User createdBy;

    @ManyToOne 
    @JoinColumn (name = "assigned_to_id")
    private User assignedTo;

}
