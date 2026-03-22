package com.campus.safety.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String role; // ADMIN, REPORTER, RECTIFIER
    
    private String realName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}