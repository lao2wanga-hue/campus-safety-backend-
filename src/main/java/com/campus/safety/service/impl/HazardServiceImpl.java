package com.campus.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.safety.dto.HazardDetailDTO;
import com.campus.safety.dto.HazardLogDTO;
import com.campus.safety.dto.HazardReportDTO;
import com.campus.safety.dto.HazardStatsDTO;
import com.campus.safety.entity.Hazard;
import com.campus.safety.entity.HazardLog;
import com.campus.safety.entity.User;
import com.campus.safety.mapper.HazardMapper;
import com.campus.safety.service.HazardLogService;
import com.campus.safety.service.HazardService;
import com.campus.safety.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HazardServiceImpl extends ServiceImpl<HazardMapper, Hazard> implements HazardService {

    @Autowired
    private HazardLogService hazardLogService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportHazard(HazardReportDTO dto, Long userId) {
        if (dto == null || userId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        Hazard hazard = new Hazard();
        BeanUtils.copyProperties(dto, hazard);
        hazard.setReporterId(userId);
        hazard.setStatus("PENDING");
        hazard.setLevel(dto.getLevel() != null ? dto.getLevel() : "NORMAL");
        
        boolean saved = this.save(hazard);
        if (!saved) {
            throw new RuntimeException("隐患上报失败");
        }
        
        hazardLogService.saveLog(hazard.getId(), userId, "上报隐患：" + dto.getTitle(), "REPORT");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignHazard(Long hazardId, Long rectifierId, Long operatorId, String comment) {
        if (hazardId == null || rectifierId == null || operatorId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        Hazard hazard = this.getById(hazardId);
        if (hazard == null) {
            throw new RuntimeException("隐患不存在，ID: " + hazardId);
        }
        
        hazard.setRectifierId(rectifierId);
        hazard.setStatus("ASSIGNED");
        
        boolean updated = this.updateById(hazard);
        if (!updated) {
            throw new RuntimeException("分配任务失败");
        }
        
        String logContent = comment != null && !comment.trim().isEmpty() ? comment : "分配整改任务";
        hazardLogService.saveLog(hazardId, operatorId, logContent, "ASSIGN");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long hazardId, String status, String comment, Long userId) {
        if (hazardId == null || status == null || userId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        Hazard hazard = this.getById(hazardId);
        if (hazard == null) {
            throw new RuntimeException("隐患不存在，ID: " + hazardId);
        }
        
        hazard.setStatus(status);
        
        boolean updated = this.updateById(hazard);
        if (!updated) {
            throw new RuntimeException("更新状态失败");
        }
        
        String logContent = comment != null && !comment.trim().isEmpty() ? comment : "状态更新为：" + status;
        hazardLogService.saveLog(hazardId, userId, logContent, status);
    }

    @Override
    public HazardDetailDTO getDetail(Long hazardId) {
        if (hazardId == null) {
            throw new IllegalArgumentException("隐患 ID 不能为空");
        }
        
        Hazard hazard = this.getById(hazardId);
        if (hazard == null) {
            throw new RuntimeException("隐患不存在，ID: " + hazardId);
        }
        
        HazardDetailDTO dto = new HazardDetailDTO();
        BeanUtils.copyProperties(hazard, dto);
        
        // 获取上报人信息
        if (hazard.getReporterId() != null) {
            User reporter = userService.getById(hazard.getReporterId());
            dto.setReporterName(reporter != null ? reporter.getRealName() : "未知");
        } else {
            dto.setReporterName("未知");
        }
        
        // 获取整改人信息
        if (hazard.getRectifierId() != null) {
            User rectifier = userService.getById(hazard.getRectifierId());
            dto.setRectifierName(rectifier != null ? rectifier.getRealName() : "未分配");
        } else {
            dto.setRectifierName("未分配");
        }
        
        // 获取日志列表
        List<HazardLog> logs = hazardLogService.getByHazardId(hazardId);
        List<HazardLogDTO> logDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(logs)) {
            logDTOs = logs.stream().map(log -> {
                HazardLogDTO logDTO = new HazardLogDTO();
                BeanUtils.copyProperties(log, logDTO);
                
                if (log.getUserId() != null) {
                    User user = userService.getById(log.getUserId());
                    logDTO.setUserName(user != null ? user.getRealName() : "未知");
                } else {
                    logDTO.setUserName("未知");
                }
                
                return logDTO;
            }).collect(Collectors.toList());
        }
        dto.setLogs(logDTOs);
        
        return dto;
    }

    @Override
    public HazardStatsDTO getStatistics() {
        HazardStatsDTO stats = new HazardStatsDTO();
        
        // 总数
        stats.setTotal(this.count());
        
        // 待处理
        LambdaQueryWrapper<Hazard> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Hazard::getStatus, "PENDING");
        stats.setPending(this.count(pendingWrapper));
        
        // 已分配
        LambdaQueryWrapper<Hazard> assignedWrapper = new LambdaQueryWrapper<>();
        assignedWrapper.eq(Hazard::getStatus, "ASSIGNED");
        stats.setAssigned(this.count(assignedWrapper));
        
        // 整改中
        LambdaQueryWrapper<Hazard> rectifyingWrapper = new LambdaQueryWrapper<>();
        rectifyingWrapper.eq(Hazard::getStatus, "RECTIFYING");
        stats.setRectifying(this.count(rectifyingWrapper));
        
        // 已完成
        LambdaQueryWrapper<Hazard> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Hazard::getStatus, "COMPLETED");
        stats.setCompleted(this.count(completedWrapper));
        
        // 已拒绝
        LambdaQueryWrapper<Hazard> rejectedWrapper = new LambdaQueryWrapper<>();
        rejectedWrapper.eq(Hazard::getStatus, "REJECTED");
        stats.setRejected(this.count(rejectedWrapper));
        
        return stats;
    }

    @Override
    public List<Hazard> listByStatus(String status) {
        LambdaQueryWrapper<Hazard> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Hazard::getStatus, status.trim());
        }
        wrapper.orderByDesc(Hazard::getCreateTime);
        return this.list(wrapper);
    }
}