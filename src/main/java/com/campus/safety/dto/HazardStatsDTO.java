package com.campus.safety.dto;

import lombok.Data;

@Data
public class HazardStatsDTO {
    private Long total;
    private Long pending;
    private Long assigned;
    private Long rectifying;
    private Long completed;
    private Long rejected;
}