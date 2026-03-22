package com.campus.safety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.safety.entity.HazardLog;
import java.util.List;

public interface HazardLogService extends IService<HazardLog> {
    void saveLog(Long hazardId, Long userId, String content, String actionType);
    List<HazardLog> getByHazardId(Long hazardId);
}