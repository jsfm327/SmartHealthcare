package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Admin;
import com.healthware.service.AdminService;
import com.healthware.vo.AdminVO;
import com.healthware.vo.LoginVO;
import com.healthware.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(adminService.adminLogin(dto));
    }

    @GetMapping("/list")
    public Result<PageResult<AdminVO>> listAdmins(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminService.listAdmins(page, size));
    }

    @PostMapping
    public Result<Void> addAdmin(@RequestBody Admin admin) {
        adminService.addAdmin(admin);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        adminService.updateAdmin(id, admin);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Result.success();
    }

    @PutMapping("/user/{id}/status")
    public Result<Void> toggleUserStatus(@PathVariable Long id, @RequestParam int status) {
        adminService.toggleUserStatus(id, status);
        return Result.success();
    }

    @GetMapping("/user/list")
    public Result<PageResult<UserVO>> listUsers(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return Result.success(adminService.listUsers(page, size));
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminService.getStatistics());
    }
}
