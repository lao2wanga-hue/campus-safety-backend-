package com.campus.safety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.safety.entity.Hazard;
import com.campus.safety.dto.HazardReportDTO;
import com.campus.safety.dto.HazardStatsDTO;
import com.campus.safety.dto.HazardDetailDTO;
import java.util.List;

public interface HazardService extends IService<Hazard> {
    void reportHazard(HazardReportDTO dto, Long userId);
    void assignHazard(Long hazardId, Long rectifierId, Long operatorId, String comment);
    void updateStatus(Long hazardId, String status, String comment, Long userId);
    HazardDetailDTO getDetail(Long hazardId);
    HazardStatsDTO getStatistics();
    List<Hazard> listByStatus(String status);
}