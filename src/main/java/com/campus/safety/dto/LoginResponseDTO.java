package com.campus.safety.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String realName;
    
    public LoginResponseDTO(String token, Long userId, String username, String role, String realName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.realName = realName;
    }
}