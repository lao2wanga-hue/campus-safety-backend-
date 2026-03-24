package com.campus.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.safety.entity.Hazard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.Map;

@Mapper
public interface HazardMapper extends BaseMapper<Hazard> {
    
    @Select("SELECT COUNT(*) FROM hazard")
    int countTotal();
    
    @Select("SELECT COUNT(*) FROM hazard WHERE status = 'PENDING'")
    int countPending();
    
    @Select("SELECT COUNT(*) FROM hazard WHERE status = 'PROCESSING'")
    int countProcessing();
    
    @Select("SELECT COUNT(*) FROM hazard WHERE status = 'RESOLVED'")
    int countResolved();
    
    @Select("SELECT level, COUNT(*) as count FROM hazard GROUP BY level")
    java.util.List<Map<String, Object>> countByLevel();
    
    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, COUNT(*) as count " +
            "FROM hazard WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') ORDER BY date")
    java.util.List<Map<String, Object>> countByDate();
}