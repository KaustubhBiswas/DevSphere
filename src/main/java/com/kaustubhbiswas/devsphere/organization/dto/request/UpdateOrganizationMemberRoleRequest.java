package com.kaustubhbiswas.devsphere.organization.dto.request;

import com.kaustubhbiswas.devsphere.organization.OrganizationRole;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateOrganizationMemberRoleRequest {
    
    @NotNull(message = "Organization role is required.")
    private OrganizationRole role;

}
