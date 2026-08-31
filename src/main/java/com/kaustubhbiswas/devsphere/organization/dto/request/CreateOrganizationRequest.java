package com.kaustubhbiswas.devsphere.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrganizationRequest {
    
    @NotBlank(message = "Organization name is required.")
    @Size(max = 100, message = "Organization name must not exceed 100 characters.")
    private String name;

    @Size(max = 500, message = "Organization description must not exceed 500 characters.")
    private String description;


}
