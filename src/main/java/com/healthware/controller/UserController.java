package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.dto.UserDTO;
import com.healthware.service.UserService;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、登录、个人信息管理")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT Token")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "通过手机号重置密码")
    public Result<Void> resetPassword(
            @Parameter(description = "手机号") @RequestParam String phone,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.resetPassword(phone, newPassword);
        return Result.success();
    }

    @GetMapping("/profile")
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的个人信息")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UserDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public Result<Void> changePassword(HttpServletRequest request,
                                       @Parameter(description = "旧密码") @RequestParam String oldPwd,
                                       @Parameter(description = "新密码") @RequestParam String newPwd) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, oldPwd, newPwd);
        return Result.success();
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录")
    public Result<Void> logout(HttpServletRequest request) {
        return Result.success();
    }
}
