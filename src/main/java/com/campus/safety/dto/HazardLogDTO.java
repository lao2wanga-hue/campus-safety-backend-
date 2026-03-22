package com.campus.safety.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HazardLogDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String content;
    private String actionType;
    private LocalDateTime createTime;
}