package com.campus.safety.service;

import com.campus.safety.dto.HazardDTO;
import com.campus.safety.dto.HazardUpdateDTO;
import com.campus.safety.entity.Hazard;
import com.campus.safety.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface HazardService {
    // 原有接口
    void create(HazardDTO dto);
    void update(Long id, HazardUpdateDTO dto);
    void assign(Long id, Long handlerId);
    void resolve(Long id, String resolution);
    void close(Long id);
    Hazard getById(Long id);
    List<Hazard> getList(String status, String level, Integer page, Integer size);
    Map<String, Object> getStatistics();
    List<Hazard> getMyReports(Long userId);
    List<Hazard> getMyTasks(Long userId);
    
    // 新增接口
    List<User> getRectifiers();
    List<Hazard> getProcessingHazards();
    void completeRepair(Long id);
    void deleteHazard(Long id);
    void updateLevel(Long id, String level);
    
    /**
     * ⭐ 上传图片
     */
    String uploadImage(MultipartFile file) throws Exception;
}