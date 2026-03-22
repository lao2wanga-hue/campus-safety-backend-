package com.campus.safety.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.safety.common.Result;
import com.campus.safety.dto.LoginDTO;
import com.campus.safety.dto.LoginResponseDTO;
import com.campus.safety.entity.User;
import com.campus.safety.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 处理用户登录、信息获取、列表查询等操作
 */
@Tag(name = "用户管理", description = "用户登录、注册、信息管理")
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginDTO 登录信息（用户名、密码）
     * @return 登录结果（包含 Token 和用户信息）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = userService.login(loginDTO);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * @param userId 从 Token 中解析的用户 ID
     * @return 用户信息（不包含密码）
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getInfo(@RequestAttribute("userId") Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                user.setPassword(null); // 不返回密码
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户列表（管理员专用）
     * @return 所有用户列表（不包含密码）
     */
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<List<User>> list() {
        try {
            List<User> users = userService.list();
            // 隐藏所有用户的密码
            if (users != null) {
                users.forEach(user -> user.setPassword(null));
            }
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据 ID 获取用户信息
     * @param id 用户 ID
     * @return 用户信息
     */
    @Operation(summary = "根据 ID 获取用户")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user != null) {
                user.setPassword(null);
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建新用户（管理员专用）
     * @param user 用户信息
     * @return 创建结果
     */
    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public Result<Boolean> createUser(@RequestBody User user) {
        try {
            // 默认密码
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword("123456");
            }
            // 默认角色
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("REPORTER");
            }
            boolean saved = userService.save(user);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息（管理员专用）
     * @param user 用户信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户")
    @PutMapping("/update")
    public Result<Boolean> updateUser(@RequestBody User user) {
        try {
            // 不允许通过接口修改密码，如需修改密码需要单独接口
            user.setPassword(null);
            boolean updated = userService.updateById(user);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户（管理员专用）
     * @param id 用户 ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        try {
            // 不允许删除自己
            // 这里可以添加逻辑判断
            boolean removed = userService.removeById(id);
            return Result.success(removed);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Operation(summary = "根据用户名查询")
    @GetMapping("/byUsername")
    public Result<User> getByUsername(@RequestParam String username) {
        try {
            User user = userService.findByUsername(username);
            if (user != null) {
                user.setPassword(null);
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}