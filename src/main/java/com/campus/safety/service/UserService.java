package com.campus.safety.service;

import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.RegisterDTO;
import com.campus.safety.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    Object login(LoginDTO loginDTO);
    
    /**
     * 获取当前用户信息
     */
    Object getUserInfo();
    
    /**
     * 获取用户列表
     */
    List<User> getUserList();
    
    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);
    
    /**
     * 根据 ID 获取用户
     */
    User getById(Long id);
}