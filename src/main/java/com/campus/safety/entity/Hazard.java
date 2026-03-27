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
    
    // ⭐ 图片（JSON 数组格式）
    private String images;
    
    // ⭐ 定位相关字段
    private String area;              // 区域（如：教学楼、宿舍楼）
    private Double latitude;          // 纬度
    private Double longitude;         // 经度
    
    private String resolution;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    private LocalDateTime resolvedAt;
}