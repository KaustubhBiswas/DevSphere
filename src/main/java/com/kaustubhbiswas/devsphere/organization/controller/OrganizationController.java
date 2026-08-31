package com.kaustubhbiswas.devsphere.organization.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaustubhbiswas.devsphere.common.response.ApiResponse;
import com.kaustubhbiswas.devsphere.organization.dto.request.AddOrganizationMemberRequest;
import com.kaustubhbiswas.devsphere.organization.dto.request.CreateOrganizationRequest;
import com.kaustubhbiswas.devsphere.organization.dto.response.OrganizationMemberResponse;
import com.kaustubhbiswas.devsphere.organization.response.OrganizationResponse;
import com.kaustubhbiswas.devsphere.organization.service.OrganizationService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService){
        this.organizationService = organizationService;
    }

    @PostMapping
    public ApiResponse<OrganizationResponse> createOrganization(@RequestBody @Valid CreateOrganizationRequest request) {
        
        OrganizationResponse response = organizationService.createOrganization(request);
        
        return ApiResponse.success("Organization created successfully.", response);
    }

    @GetMapping("/id")
    public ApiResponse<OrganizationResponse> getOrganizationById(@PathVariable Long id) {

        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ApiResponse.success("Organizaion with id: "+id+" found!", response);
    }

    @GetMapping
    public ApiResponse<List<OrganizationResponse>> getMyOrganizations() {

        List<OrganizationResponse> organizations = organizationService.getMyOrganizations();

        return ApiResponse.success("Organizations fetched successfully.", organizations);
    }
    
    @PostMapping("/{organizationId}/members")
    public ApiResponse<Void> addMember(@PathVariable Long organizationId, @Valid @RequestBody AddOrganizationMemberRequest request) {

        organizationService.addMember(organizationId, request);
        
        return ApiResponse.success("Member added successfully.", null);
    }
    
    
    @GetMapping("/{organizationId}/members")
    public ApiResponse<List<OrganizationMemberResponse>> getMembers(@PathVariable Long organizationId) {

        List<OrganizationMemberResponse> members = organizationService.getMembers(organizationId);

        return ApiResponse.success("Members fetched successfully.", members);
    }
    
    
    
}
