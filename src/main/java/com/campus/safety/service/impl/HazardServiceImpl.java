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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HazardServiceImpl implements HazardService {
    
    private final HazardMapper hazardMapper;
    private final HazardRecordMapper hazardRecordMapper;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    // ⭐ 图片上传目录
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
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
        
        // ⭐ 处理图片
        hazard.setImages(dto.getImages());
        
        // ⭐ 处理定位信息
        hazard.setArea(dto.getArea());
        hazard.setLatitude(dto.getLatitude());
        hazard.setLongitude(dto.getLongitude());
        
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
    
    /**
     * ⭐ 上传图片
     */
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 验证文件类型
        List<String> allowedTypes = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedTypes.contains(extension.toLowerCase())) {
            throw new BusinessException("只支持 jpg、png、gif 格式图片");
        }
        
        // 验证文件大小（最大 5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("图片大小不能超过 5MB");
        }
        
        // 生成文件名
        String filename = UUID.randomUUID().toString() + extension;
        
        // 创建上传目录
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 保存文件
        Path filePath = uploadDir.resolve(filename);
        file.transferTo(filePath);
        
        // ⭐ 返回访问 URL
        return "/api/hazard/images/" + filename;
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
    public void update(Long id, HazardUpdateDTO dto) {
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        Long userId = jwtUtil.getCurrentUserId();
        User user = userMapper.selectById(userId);
        
        if (!"ADMIN".equals(user.getRole()) && !userId.equals(hazard.getReporterId())) {
            throw new BusinessException("无权修改该隐患");
        }
        
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
        
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(userId);
        record.setOperatorName(user.getRealName());
        record.setAction("UPDATE");
        record.setContent("更新隐患状态");
        hazardRecordMapper.insert(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long id, Long handlerId) {
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("只有管理员可以分配隐患");
        }
        
        User handler = userMapper.selectById(handlerId);
        if (handler == null || !"RECTIFIER".equals(handler.getRole())) {
            throw new BusinessException("无效的维修员");
        }
        
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        hazard.setStatus("PROCESSING");
        hazard.setHandlerId(handlerId);
        hazard.setHandlerName(handler.getRealName());
        hazard.setUpdatedAt(LocalDateTime.now());
        hazardMapper.updateById(hazard);
        
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(currentUserId);
        record.setOperatorName(currentUser.getRealName());
        record.setAction("ASSIGN");
        record.setContent("分配给维修员：" + handler.getRealName());
        hazardRecordMapper.insert(record);
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
    
    @Override
    public List<User> getRectifiers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getRole, "RECTIFIER");
        return userMapper.selectList(wrapper);
    }
    
    @Override
    public List<Hazard> getProcessingHazards() {
        LambdaQueryWrapper<Hazard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hazard::getStatus, "PROCESSING");
        wrapper.orderByDesc(Hazard::getUpdatedAt);
        return hazardMapper.selectList(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeRepair(Long id) {
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"RECTIFIER".equals(currentUser.getRole())) {
            throw new BusinessException("只有维修员可以完成修理");
        }
        
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        if (!"PROCESSING".equals(hazard.getStatus())) {
            throw new BusinessException("隐患状态不正确");
        }
        
        if (!currentUserId.equals(hazard.getHandlerId())) {
            throw new BusinessException("只能完成自己负责的隐患");
        }
        
        hazard.setStatus("RESOLVED");
        hazard.setResolution("修理完成");
        hazard.setResolvedAt(LocalDateTime.now());
        hazard.setUpdatedAt(LocalDateTime.now());
        hazardMapper.updateById(hazard);
        
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(currentUserId);
        record.setOperatorName(currentUser.getRealName());
        record.setAction("COMPLETE");
        record.setContent("完成修理");
        hazardRecordMapper.insert(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHazard(Long id) {
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("只有管理员可以删除隐患");
        }
        
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        hazardMapper.deleteById(id);
        
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(currentUserId);
        record.setOperatorName(currentUser.getRealName());
        record.setAction("DELETE");
        record.setContent("删除隐患：" + hazard.getTitle());
        hazardRecordMapper.insert(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLevel(Long id, String level) {
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("只有管理员可以调整隐患等级");
        }
        
        Hazard hazard = hazardMapper.selectById(id);
        if (hazard == null) {
            throw new BusinessException("隐患不存在");
        }
        
        if (!"LOW".equals(level) && !"MEDIUM".equals(level) && !"HIGH".equals(level)) {
            throw new BusinessException("无效的等级");
        }
        
        String oldLevel = hazard.getLevel();
        
        hazard.setLevel(level);
        hazard.setUpdatedAt(LocalDateTime.now());
        hazardMapper.updateById(hazard);
        
        HazardRecord record = new HazardRecord();
        record.setHazardId(id);
        record.setOperatorId(currentUserId);
        record.setOperatorName(currentUser.getRealName());
        record.setAction("UPDATE_LEVEL");
        record.setContent("调整等级：" + oldLevel + " → " + level);
        hazardRecordMapper.insert(record);
    }
}