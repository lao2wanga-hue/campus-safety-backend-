package com.campus.safety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.safety.entity.User;
import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.LoginResponseDTO;

public interface UserService extends IService<User> {
    /**
     * 用户登录
     */
    LoginResponseDTO login(LoginDTO loginDTO);
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
}