package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AssignDTO {
    @NotNull(message = "隐患 ID 不能为空")
    private Long hazardId;
    
    @NotNull(message = "整改人 ID 不能为空")
    private Long rectifierId;
    
    private String comment;
}