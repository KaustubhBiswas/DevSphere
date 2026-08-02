package com.kaustubhbiswas.devsphere.auth.dto.response;

import com.kaustubhbiswas.devsphere.user.Role;

public class LoginUserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String token;

    public LoginUserResponse(){}

    public void setId(Long id){
        this.id = id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setRole(Role role){
        this.role =  role;
    }

    public Long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public Role getRole(){
        return role;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return  token;
    }
}
