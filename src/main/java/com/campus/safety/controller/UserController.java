package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.RegisterDTO;
import com.campus.safety.dto.UserUpdateDTO;
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
     * ⭐ 更新用户（管理员权限）
     */
    @PutMapping("/{id}")
    public Result<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        userService.updateUser(id, dto);
        return Result.success("修改成功");
    }
    
    /**
     * ⭐ 删除用户（管理员权限）
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}