package com.campus.safety.controller;

import com.campus.safety.common.Result;
import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.RegisterDTO;
import com.campus.safety.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// ⭐ 使用 jakarta.validation 而不是 javax.validation
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户管理")
public class UserController {
    
    private final UserService userService;
    
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
}