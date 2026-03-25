package com.campus.safety.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.safety.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(String username);
    
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectById(Long id);
}