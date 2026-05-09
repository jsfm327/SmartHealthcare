package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Admin;
import com.healthware.service.AdminService;
import com.healthware.vo.AdminVO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员管理", description = "管理员登录、用户管理、系统统计")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理员登录获取JWT Token")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(adminService.adminLogin(dto));
    }

    @GetMapping("/list")
    @Operation(summary = "管理员列表", description = "分页获取管理员列表")
    public Result<PageResult<AdminVO>> listAdmins(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminService.listAdmins(page, size));
    }

    @PostMapping
    @Operation(summary = "添加管理员", description = "添加新的管理员")
    public Result<Void> addAdmin(@RequestBody Admin admin) {
        adminService.addAdmin(admin);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新管理员", description = "更新管理员信息")
    public Result<Void> updateAdmin(
            @Parameter(description = "管理员ID") @PathVariable Long id,
            @RequestBody Admin admin) {
        adminService.updateAdmin(id, admin);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员", description = "删除指定管理员")
    public Result<Void> deleteAdmin(
            @Parameter(description = "管理员ID") @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success();
    }

    @PutMapping("/user/{id}/status")
    @Operation(summary = "切换用户状态", description = "启用/禁用用户")
    public Result<Void> toggleUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态: 0-禁用, 1-启用") @RequestParam int status) {
        adminService.toggleUserStatus(id, status);
        return Result.success();
    }

    @GetMapping("/user/list")
    @Operation(summary = "用户列表", description = "分页获取用户列表")
    public Result<PageResult<UserVO>> listUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminService.listUsers(page, size));
    }

    @GetMapping("/statistics")
    @Operation(summary = "系统统计", description = "获取系统统计数据")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminService.getStatistics());
    }
}
