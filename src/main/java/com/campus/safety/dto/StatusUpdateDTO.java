package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class StatusUpdateDTO {
    @NotNull(message = "隐患 ID 不能为空")
    private Long hazardId;
    
    @NotBlank(message = "状态不能为空")
    private String status;
    
    private String comment;
}