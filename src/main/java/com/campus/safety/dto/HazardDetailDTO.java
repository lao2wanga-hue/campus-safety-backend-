package com.campus.safety.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HazardDetailDTO {
    private Long id;
    private String title;
    private String location;
    private String description;
    private String imageUrl;
    private String level;
    private String status;
    private String reporterName;
    private String rectifierName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<HazardLogDTO> logs;
}