package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.entity.User;
import com.campus.safety.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/temp")
@RequiredArgsConstructor
public class TempFixController {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * ⭐ 临时接口：修复所有用户密码（无需认证）
     * 使用后请立即删除此接口！
     */
    @GetMapping("/fix-passwords")
    public Result<?> fixPasswords() {
        try {
            String encodedPassword = passwordEncoder.encode("123456");
            System.out.println("=== 修复所有用户密码 ===");
            System.out.println("Encoded: " + encodedPassword);
            System.out.println("Length: " + encodedPassword.length());
            
            // 修复 admin
            User admin = userMapper.selectByUsername("admin");
            if (admin != null) {
                admin.setPassword(encodedPassword);
                userMapper.updateById(admin);
                System.out.println("✓ admin 密码已修复");
            }
            
            // 修复 teacher
            User teacher = userMapper.selectByUsername("teacher");
            if (teacher != null) {
                teacher.setPassword(encodedPassword);
                userMapper.updateById(teacher);
                System.out.println("✓ teacher 密码已修复");
            }
            
            // 修复 worker
            User worker = userMapper.selectByUsername("worker");
            if (worker != null) {
                worker.setPassword(encodedPassword);
                userMapper.updateById(worker);
                System.out.println("✓ worker 密码已修复");
            }
            
            System.out.println("=== 所有用户密码修复完成 ===");
            return Result.success("修复成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修复失败: " + e.getMessage());
        }
    }
    
    /**
     * ⭐ 临时接口：创建 admin 用户
     */
    @GetMapping("/create-admin")
    public Result<?> createAdmin() {
        try {
            User existing = userMapper.selectByUsername("admin");
            
            if (existing == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRealName("系统管理员");
                admin.setRole("ADMIN");
                userMapper.insert(admin);
                System.out.println("✓ admin 用户已创建");
                return Result.success();
            }
            
            return Result.success("用户已存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建失败: " + e.getMessage());
        }
    }
}