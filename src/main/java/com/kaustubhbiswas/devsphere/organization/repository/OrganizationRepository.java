package com.kaustubhbiswas.devsphere.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaustubhbiswas.devsphere.organization.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByOwnerId(Long ownerId);
}
