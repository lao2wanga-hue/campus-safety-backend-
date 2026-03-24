package com.campus.safety.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hazard_record")
public class HazardRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hazardId;
    
    private Long operatorId;
    
    private String operatorName;
    
    private String action;
    
    private String content;
    
    private LocalDateTime createdAt;
}