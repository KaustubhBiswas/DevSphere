package com.kaustubhbiswas.devsphere.organization.dto.request;

import com.kaustubhbiswas.devsphere.organization.OrganizationRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddOrganizationMemberRequest {
    
    @NotBlank(message = "User email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotNull(message = "Organization role is required.")
    private OrganizationRole role;

}
