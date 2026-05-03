package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.dto.LoginDTO;
import com.healthware.dto.RegisterDTO;
import com.healthware.dto.UserDTO;
import com.healthware.service.UserService;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam String phone, @RequestParam String newPassword) {
        userService.resetPassword(phone, newPassword);
        return Result.success();
    }

    @GetMapping("/profile")
    public Result<UserVO> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody UserDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request,
                                       @RequestParam String oldPwd, @RequestParam String newPwd) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, oldPwd, newPwd);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        // Token 失效由前端处理，后端可选择清除 Redis
        return Result.success();
    }
}
