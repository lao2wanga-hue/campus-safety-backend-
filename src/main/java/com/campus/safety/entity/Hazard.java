package com.campus.safety.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hazard")
public class Hazard {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String description;
    
    private String location;
    
    private String level;
    
    private String status;
    
    private Long reporterId;
    
    private String reporterName;
    
    private Long handlerId;
    
    private String handlerName;
    
    private String images;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime resolvedAt;
    
    private String resolution;
}