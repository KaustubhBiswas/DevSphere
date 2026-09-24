package com.kaustubhbiswas.devsphere.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaustubhbiswas.devsphere.task.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
    List<Task> findByProjectId(Long projectId);
}
