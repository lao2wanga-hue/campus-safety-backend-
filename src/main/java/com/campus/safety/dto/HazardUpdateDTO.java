package com.campus.safety.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class HazardUpdateDTO {
    
    private String status;
    private String resolution;
    private Long handlerId;
}