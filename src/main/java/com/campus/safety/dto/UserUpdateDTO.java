package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UserUpdateDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "姓名不能为空")
    private String realName;
    
    @NotBlank(message = "角色不能为空")
    private String role;
}