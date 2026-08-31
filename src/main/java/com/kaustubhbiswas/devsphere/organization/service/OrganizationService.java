package com.kaustubhbiswas.devsphere.organization.service;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kaustubhbiswas.devsphere.common.exception.BusinessValidationException;
import com.kaustubhbiswas.devsphere.common.exception.ResourceNotFoundException;
import com.kaustubhbiswas.devsphere.organization.Organization;
import com.kaustubhbiswas.devsphere.organization.OrganizationMember;
import com.kaustubhbiswas.devsphere.organization.OrganizationRole;
import com.kaustubhbiswas.devsphere.organization.repository.OrganizationMemberRepository;
import com.kaustubhbiswas.devsphere.organization.repository.OrganizationRepository;
import com.kaustubhbiswas.devsphere.organization.dto.request.AddOrganizationMemberRequest;
import com.kaustubhbiswas.devsphere.organization.dto.request.CreateOrganizationRequest;
import com.kaustubhbiswas.devsphere.organization.dto.response.OrganizationMemberResponse;
import com.kaustubhbiswas.devsphere.organization.response.OrganizationResponse;
import com.kaustubhbiswas.devsphere.user.User;
import com.kaustubhbiswas.devsphere.user.UserRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationService(OrganizationRepository organizationRepository, UserRepository userRepository, OrganizationMemberRepository organizationMemberRepository){
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public OrganizationResponse createOrganization(CreateOrganizationRequest request){
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User owner = userRepository.findByEmail(email).orElseThrow(() -> new BusinessValidationException("User not found."));

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setDescription(request.getDescription());
        organization.setOwner(owner);

        Organization savedOrganization = organizationRepository.save(organization);

        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setOrganization(savedOrganization);
        organizationMember.setUser(owner);
        organizationMember.setRole(OrganizationRole.OWNER);

        organizationMemberRepository.save(organizationMember);

        return toResponse(savedOrganization);
    }

    private OrganizationResponse toResponse(Organization organization){

        OrganizationResponse response = new OrganizationResponse();

        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setDescription(organization.getDescription());
        response.setOwnerId(organization.getOwner().getId());
        response.setOwnerUsername(organization.getOwner().getUsername());
        response.setCreatedAt(organization.getCreatedAt());
        response.setUpdatedAt(organization.getUpdatedAt());

        return response;

    }

    public OrganizationResponse getOrganizationById(Long id){
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: " + id));
        
        return toResponse(organization);
    }

    public List<OrganizationResponse> getMyOrganizations(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return organizationRepository.findByOwnerId(user.getId()).stream().map(this::toResponse).toList();
        
    }


    public void addMember(Long organizationId, AddOrganizationMemberRequest request){
    
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User requester = userRepository.findByEmail(email).orElseThrow(() -> new BusinessValidationException("User not found."));

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Can't find organization with id: "+ organizationId));

        OrganizationMember requesterMembership = organizationMemberRepository.findByOrganizationIdAndUserId(organization.getId(), requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        if (requesterMembership.getRole()!=OrganizationRole.OWNER && requesterMembership.getRole()!=OrganizationRole.ADMIN){
            throw new BusinessValidationException("You do not have permission to add members.");
        }

        User targetUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceNotFoundException("user not found with email: "+request.getEmail()));

        if (organizationMemberRepository.existsByOrganizationIdAndUserId(organization.getId(), targetUser.getId())){
            throw new BusinessValidationException("User is already a member of this organization.");
        }

        if (request.getRole()==OrganizationRole.OWNER){
            throw new BusinessValidationException("Cannot assign OWNER role to a new member.");
        }

        if (requesterMembership.getRole()==OrganizationRole.ADMIN && request.getRole()==OrganizationRole.ADMIN){
            throw new BusinessValidationException("Admin cannot assign ADMIN role to another member.");
        }

        OrganizationMember newMember = new OrganizationMember();
        newMember.setOrganization(organization);
        newMember.setUser(targetUser);
        newMember.setRole(request.getRole());

        organizationMemberRepository.save(newMember);
    }

    public List<OrganizationMemberResponse> getMembers(Long organizationId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User requester = userRepository.findByEmail(email).orElseThrow(() -> new BusinessValidationException("User not found."));

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ResourceNotFoundException("Organization not found with id: "+organizationId));

        organizationMemberRepository.findByOrganizationIdAndUserId(organization.getId(), requester.getId()).orElseThrow(() -> new BusinessValidationException("You are not a member of this organization."));

        return organizationMemberRepository.findByOrganizationId(organization.getId()).stream().map(this::toMemberResponse).toList();
    }
    
    private OrganizationMemberResponse toMemberResponse(OrganizationMember member){

        OrganizationMemberResponse response = new OrganizationMemberResponse();

        response.setUserId(member.getUser().getId());
        response.setUsername(member.getUser().getUsername());
        response.setEmail(member.getUser().getEmail());
        response.setRole(member.getRole());
        response.setJoinedAt(member.getJoinedAt());

        return response;
    }

}
