package com.campus.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.safety.entity.Hazard;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HazardMapper extends BaseMapper<Hazard> {
}