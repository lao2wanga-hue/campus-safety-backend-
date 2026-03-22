package com.campus.safety.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("hazard_log")
public class HazardLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long hazardId;
    
    private Long userId;
    
    private String content;
    
    private String actionType; // REPORT, ASSIGN, START, COMPLETE, REJECT
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}