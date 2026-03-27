package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class HazardDTO {
    
    @NotBlank(message = "隐患标题不能为空")
    private String title;
    
    @NotBlank(message = "隐患描述不能为空")
    private String description;
    
    @NotBlank(message = "隐患位置不能为空")
    private String location;
    
    @NotNull(message = "隐患等级不能为空")
    private String level;
    
    // ⭐ 图片（JSON 字符串或数组）
    private String images;
    
    // ⭐ 定位相关
    private String area;
    private Double latitude;
    private Double longitude;
}