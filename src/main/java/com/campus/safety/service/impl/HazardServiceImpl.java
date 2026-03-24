package com.campus.safety.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.safety.dto.HazardDTO;
import com.campus.safety.dto.HazardUpdateDTO;
import com.campus.safety.entity.Hazard;
import com.campus.safety.entity.HazardRecord;
import com.campus.safety.entity.User;
import com.campus.safety.exception.BusinessException;
import com.campus.safety.mapper.HazardMapper;
import com.campus.safety.mapper.HazardRecordMapper;
import com.campus.safety.mapper.UserMapper;
import com.campus.safety.service.HazardService;
import com.campus.safety.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HazardServiceImpl implements HazardService {
    
    private final HazardMapper hazardMapper;
    private final HazardRecordMapper hazardRecordMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(HazardDTO dto) {
        Long userId = jwtUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        
        Hazard hazard = new Hazard();
        hazard.setTitle(dto.getTitle());
        hazard.setDescription(dto.getDescription());
        hazard.setLocation(dto.getLocation());
        hazard.setLevel(dto.getLevel());
        hazard.setStatus("PENDING");
        hazard.setReporterId(userId);
        hazard.setReporterName(user.getRealName());
        hazard.setImages(dto.getImages());
        
        hazardMapper.insert(hazard);
        
        // 创建记录
        HazardRecord record = new HazardRecord();
        record.setHazardId(hazard.getId());
        record.setOperatorId(userId);
        record.setOperatorName(user.getRealName());
        record.setAction("CREATE");
        record.setContent("创建隐患：" + dto.getTitle());
        hazardRecordMapper.insert(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, HazardUpdateDTO dto) {
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        Long userId = jwtUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        
        if (dto.getStatus() != null) {
            hazard.setStatus(dto.getStatus());
        }
        if (dto.getResolution() != null) {
            hazard.setResolution(dto.getResolution());
            hazard.setResolvedAt(LocalDateTime.now());
        }
        if (dto.getHandlerId() != null) {
            hazard.setHandlerId(dto.getHandlerId());
            User handler = userMapper.selectById(dto.getHandlerId());
            hazard.setHandlerName(handler != null ? handler.getRealName() : null);
        }
        
        hazardMapper.updateById(hazard);
        
        // 创建记录
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(userId);
        record.setOperatorName(user.getRealName());
        record.setAction("UPDATE");
        record.setContent("更新隐患状态");
        hazardRecordMapper.insert(record);
    }
    
    @Override
    public void assign(Long id, Long handlerId) {
        HazardUpdateDTO dto = new HazardUpdateDTO();
        dto.setStatus("PROCESSING");
        dto.setHandlerId(handlerId);
        update(id, dto);
    }
    
    @Override
    public void resolve(Long id, String resolution) {
        HazardUpdateDTO dto = new HazardUpdateDTO();
        dto.setStatus("RESOLVED");
        dto.setResolution(resolution);
        update(id, dto);
    }
    
    @Override
    public void close(Long id) {
        HazardUpdateDTO dto = new HazardUpdateDTO();
        dto.setStatus("CLOSED");
        update(id, dto);
    }
    
    @Override
    public Hazard getById(Long id) {
        return hazardMapper.selectById(id);
    }
    
    @Override
    public List<Hazard> getList(String status, String level, Integer page, Integer size) {
        LambdaQueryWrapper<Hazard> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Hazard::getStatus, status);
        }
        if (level != null && !level.isEmpty()) {
            wrapper.eq(Hazard::getLevel, level);
        }
        wrapper.orderByDesc(Hazard::getCreatedAt);
        
        if (page != null && size != null) {
            Page<Hazard> hazardPage = new Page<>(page, size);
            return hazardMapper.selectPage(hazardPage, wrapper).getRecords();
        }
        return hazardMapper.selectList(wrapper);
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", hazardMapper.countTotal());
        stats.put("pending", hazardMapper.countPending());
        stats.put("processing", hazardMapper.countProcessing());
        stats.put("resolved", hazardMapper.countResolved());
        stats.put("byLevel", hazardMapper.countByLevel());
        stats.put("byDate", hazardMapper.countByDate());
        return stats;
    }
    
    @Override
    public List<Hazard> getMyReports(Long userId) {
        LambdaQueryWrapper<Hazard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hazard::getReporterId, userId);
        wrapper.orderByDesc(Hazard::getCreatedAt);
        return hazardMapper.selectList(wrapper);
    }
    
    @Override
    public List<Hazard> getMyTasks(Long userId) {
        LambdaQueryWrapper<Hazard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hazard::getHandlerId, userId);
        wrapper.ne(Hazard::getStatus, "CLOSED");
        wrapper.orderByDesc(Hazard::getCreatedAt);
        return hazardMapper.selectList(wrapper);
    }
}