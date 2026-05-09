package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.PatientDTO;
import com.healthware.service.PatientService;
import com.healthware.vo.PatientVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@Tag(name = "患者管理", description = "就诊人管理，支持一个用户管理多个就诊人")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/my")
    @Operation(summary = "我的就诊人", description = "获取当前用户的所有就诊人")
    public Result<List<PatientVO>> myPatients(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(patientService.myPatients(userId));
    }

    @PostMapping
    @Operation(summary = "添加就诊人", description = "添加新的就诊人")
    public Result<Void> add(HttpServletRequest request, @Valid @RequestBody PatientDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        patientService.addPatient(userId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新就诊人", description = "更新就诊人信息")
    public Result<Void> update(
            @Parameter(description = "就诊人ID") @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto) {
        patientService.updatePatient(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除就诊人", description = "删除指定就诊人")
    public Result<Void> delete(HttpServletRequest request,
                               @Parameter(description = "就诊人ID") @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        patientService.deletePatient(id, userId);
        return Result.success();
    }

    @GetMapping("/list")
    @Operation(summary = "就诊人列表", description = "分页获取所有就诊人（管理端）")
    public Result<PageResult<PatientVO>> listAll(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(patientService.listAll(page, size));
    }
}
