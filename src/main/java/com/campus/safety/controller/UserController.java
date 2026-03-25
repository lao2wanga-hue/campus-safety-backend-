package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.RegisterDTO;
import com.campus.safety.entity.User;
import com.campus.safety.mapper.UserMapper;
import com.campus.safety.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {
    
    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功");
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo() {
        return Result.success(userService.getUserInfo());
    }
    
    /**
     * 获取用户列表（管理员）
     */
    @GetMapping("/list")
    public Result<?> getUserList() {
        return Result.success(userService.getUserList());
    }
    
    /**
     * ⭐ 临时接口：修复 admin 密码（部署后访问一次即可）
     */
    @PostMapping("/fix-admin-password")
    public Result<Void> fixAdminPassword() {
        try {
            User admin = userMapper.selectByUsername("admin");
            
            if (admin != null) {
                String encodedPassword = passwordEncoder.encode("123456");
                System.out.println("=== 修复 admin 密码 ===");
                System.out.println("Encoded password: " + encodedPassword);
                System.out.println("Encoded password length: " + encodedPassword.length());
                
                admin.setPassword(encodedPassword);
                userMapper.updateById(admin);
                
                System.out.println("admin 密码已修复成功");
                return Result.success();
            }
            
            System.out.println("admin 用户不存在");
            return Result.error("admin 用户不存在");
        } catch (Exception e) {
            System.err.println("修复密码失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("修复失败: " + e.getMessage());
        }
    }
    
    /**
     * ⭐ 临时接口：创建 admin 用户（如果不存在）
     */
   @PostMapping("/create-admin-if-not-exists")
public Result<?> createAdminIfNotExists() {  // ⭐ 改为 Result<?>
    try {
        User existing = userMapper.selectByUsername("admin");
        
        if (existing == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            userMapper.insert(admin);
            System.out.println("admin 用户已创建成功");
            return Result.success();
        }
        
        System.out.println("admin 用户已存在");
        return Result.success("用户已存在");  // ✅ 现在可以返回 String
    } catch (Exception e) {
        System.err.println("创建 admin 失败: " + e.getMessage());
        e.printStackTrace();
        return Result.error("创建失败: " + e.getMessage());
    }
}
    
    /**
     * ⭐ 临时接口：修复所有用户密码
     */
    @PostMapping("/fix-all-user-passwords")
    public Result<Void> fixAllUserPasswords() {
        try {
            String encodedPassword = passwordEncoder.encode("123456");
            System.out.println("=== 修复所有用户密码 ===");
            System.out.println("Encoded password: " + encodedPassword);
            
            // 修复 admin
            User admin = userMapper.selectByUsername("admin");
            if (admin != null) {
                admin.setPassword(encodedPassword);
                userMapper.updateById(admin);
                System.out.println("admin 密码已修复");
            }
            
            // 修复 teacher
            User teacher = userMapper.selectByUsername("teacher");
            if (teacher != null) {
                teacher.setPassword(encodedPassword);
                userMapper.updateById(teacher);
                System.out.println("teacher 密码已修复");
            }
            
            // 修复 worker
            User worker = userMapper.selectByUsername("worker");
            if (worker != null) {
                worker.setPassword(encodedPassword);
                userMapper.updateById(worker);
                System.out.println("worker 密码已修复");
            }
            
            System.out.println("所有用户密码修复完成");
            return Result.success();
        } catch (Exception e) {
            System.err.println("修复所有用户密码失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("修复失败: " + e.getMessage());
        }
    }
}