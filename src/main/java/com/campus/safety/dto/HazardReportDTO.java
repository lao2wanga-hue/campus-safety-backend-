package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class HazardReportDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "地点不能为空")
    private String location;
    
    private String description;
    
    private String imageUrl;
    
    private String level;
}