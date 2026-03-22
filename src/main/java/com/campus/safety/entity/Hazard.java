package com.campus.safety.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("safety_hazard")
public class Hazard {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String location;
    
    private String description;
    
    private String imageUrl;
    
    private String level; // NORMAL, HIGH, URGENT
    
    private String status; // PENDING, ASSIGNED, RECTIFYING, COMPLETED, REJECTED
    
    private Long reporterId;
    
    private Long rectifierId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}