package com.kaustubhbiswas.devsphere.organization.dto.response;

import java.time.LocalDateTime;

import com.kaustubhbiswas.devsphere.organization.OrganizationRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrganizationMemberResponse {
    
    private Long userId;
    private String username;
    private String email;
    private OrganizationRole role;
    private LocalDateTime joinedAt;

}
