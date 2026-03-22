package com.campus.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.safety.entity.HazardLog;
import com.campus.safety.mapper.HazardLogMapper;
import com.campus.safety.service.HazardLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HazardLogServiceImpl extends ServiceImpl<HazardLogMapper, HazardLog> implements HazardLogService {

    @Override
    public void saveLog(Long hazardId, Long userId, String content, String actionType) {
        HazardLog log = new HazardLog();
        log.setHazardId(hazardId);
        log.setUserId(userId);
        log.setContent(content);
        log.setActionType(actionType);
        log.setCreateTime(LocalDateTime.now());
        this.save(log);
    }

    @Override
    public List<HazardLog> getByHazardId(Long hazardId) {
        LambdaQueryWrapper<HazardLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HazardLog::getHazardId, hazardId);
        wrapper.orderByAsc(HazardLog::getCreateTime);
        return this.list(wrapper);
    }
}