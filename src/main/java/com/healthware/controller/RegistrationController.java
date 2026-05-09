package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.AppointmentDTO;
import com.healthware.service.RegistrationService;
import com.healthware.vo.RegistrationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
@Tag(name = "挂号管理", description = "预约挂号、排队叫号、就诊确认")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/appointment")
    @Operation(summary = "预约挂号", description = "创建预约挂号")
    public Result<RegistrationVO> appointment(HttpServletRequest request, @Valid @RequestBody AppointmentDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.createAppointment(userId, dto));
    }

    @GetMapping("/my")
    @Operation(summary = "我的挂号", description = "获取当前用户的挂号记录")
    public Result<PageResult<RegistrationVO>> myRegistrations(HttpServletRequest request,
                                                               @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
                                                               @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.myRegistrations(userId, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "挂号详情", description = "获取挂号详细信息")
    public Result<RegistrationVO> getDetail(
            @Parameter(description = "挂号ID") @PathVariable Long id) {
        return Result.success(registrationService.getDetail(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消挂号", description = "取消预约挂号")
    public Result<Void> cancel(HttpServletRequest request,
                               @Parameter(description = "挂号ID") @PathVariable Long id,
                               @Parameter(description = "取消原因") @RequestParam(required = false) String reason) {
        Long userId = (Long) request.getAttribute("userId");
        registrationService.cancelRegistration(id, userId, reason);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "挂号列表", description = "分页获取所有挂号记录，可按日期和科室筛选")
    public Result<PageResult<RegistrationVO>> listAll(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "日期") @RequestParam(required = false) String date,
            @Parameter(description = "科室ID") @RequestParam(required = false) Long deptId) {
        return Result.success(registrationService.listAll(page, size, date, deptId));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认就诊", description = "确认患者已就诊")
    public Result<Void> confirmVisit(
            @Parameter(description = "挂号ID") @PathVariable Long id) {
        registrationService.confirmVisit(id);
        return Result.success();
    }
}
