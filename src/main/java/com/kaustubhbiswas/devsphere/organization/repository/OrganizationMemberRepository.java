package com.kaustubhbiswas.devsphere.organization.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kaustubhbiswas.devsphere.organization.OrganizationMember;

public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long>{
    
    boolean existsByOrganizationIdAndUserId(Long organizationId, Long userId);

    Optional<OrganizationMember> findByOrganizationIdAndUserId(Long organizationId, Long userId);

    List<OrganizationMember> findByOrganizationId(Long organizationId);

}
