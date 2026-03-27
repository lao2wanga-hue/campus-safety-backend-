package com.campus.safety.service.impl;

import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.RegisterDTO;
import com.campus.safety.dto.UserUpdateDTO;
import com.campus.safety.entity.User;
import com.campus.safety.exception.BusinessException;
import com.campus.safety.mapper.UserMapper;
import com.campus.safety.service.UserService;
import com.campus.safety.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public Object login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        
        return result;
    }
    
    @Override
    public Object getUserInfo() {
        Long userId = jwtUtil.getCurrentUserId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        
        return result;
    }
    
    @Override
    public List<User> getUserList() {
        return userMapper.selectList(null);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证角色
        if (!"REPORTER".equals(registerDTO.getRole()) && 
            !"RECTIFIER".equals(registerDTO.getRole())) {
            throw new BusinessException("角色只能是 REPORTER 或 RECTIFIER");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRealName(registerDTO.getRealName());
        user.setRole(registerDTO.getRole());
        
        userMapper.insert(user);
    }
    
    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * ⭐ 更新用户（管理员权限）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateDTO dto) {
        // 验证权限 - 只有管理员可以修改用户
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("只有管理员可以修改用户");
        }
        
        // 验证用户存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证用户名不重复（排除自己）
        User existingUser = userMapper.selectByUsername(dto.getUsername());
        if (existingUser != null && !existingUser.getId().equals(id)) {
            throw new BusinessException("用户名已存在");
        }
        
        // 验证角色
        if (!"ADMIN".equals(dto.getRole()) && 
            !"REPORTER".equals(dto.getRole()) && 
            !"RECTIFIER".equals(dto.getRole())) {
            throw new BusinessException("角色无效");
        }
        
        // 更新用户
        user.setUsername(dto.getUsername());
        user.setRealName(dto.getRealName());
        user.setRole(dto.getRole());
        userMapper.updateById(user);
    }
    
    /**
     * ⭐ 删除用户（管理员权限）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        // 验证权限 - 只有管理员可以删除用户
        Long currentUserId = jwtUtil.getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        
        if (!"ADMIN".equals(currentUser.getRole())) {
            throw new BusinessException("只有管理员可以删除用户");
        }
        
        // 验证用户存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 不能删除自己
        if (user.getId().equals(currentUserId)) {
            throw new BusinessException("不能删除自己");
        }
        
        // 删除用户
        userMapper.deleteById(id);
    }
}