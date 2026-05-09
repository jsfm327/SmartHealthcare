package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.LoginDTO;
import com.healthware.entity.Doctor;
import com.healthware.service.DoctorService;
import com.healthware.service.RegistrationService;
import com.healthware.vo.DoctorLoginVO;
import com.healthware.vo.DoctorVO;
import com.healthware.vo.RegistrationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@Tag(name = "医生管理", description = "医生登录、接诊管理")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/list")
    @Operation(summary = "医生列表", description = "分页获取医生列表，可按科室筛选")
    public Result<PageResult<DoctorVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "科室ID") @RequestParam(required = false) Long deptId) {
        return Result.success(doctorService.listDoctors(page, size, deptId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "医生详情", description = "获取医生详细信息")
    public Result<DoctorVO> getDetail(
            @Parameter(description = "医生ID") @PathVariable Long id) {
        return Result.success(doctorService.getDetail(id));
    }

    @GetMapping("/department/{deptId}")
    @Operation(summary = "按科室查询医生", description = "获取指定科室下的所有医生")
    public Result<List<DoctorVO>> listByDepartment(
            @Parameter(description = "科室ID") @PathVariable Long deptId) {
        return Result.success(doctorService.listByDepartment(deptId));
    }

    @PostMapping
    @Operation(summary = "添加医生", description = "添加新的医生")
    public Result<Void> add(@RequestBody Doctor doctor) {
        doctorService.addDoctor(doctor);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新医生", description = "更新医生信息")
    public Result<Void> update(
            @Parameter(description = "医生ID") @PathVariable Long id,
            @RequestBody Doctor doctor) {
        doctorService.updateDoctor(id, doctor);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除医生", description = "删除指定医生")
    public Result<Void> delete(
            @Parameter(description = "医生ID") @PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "医生登录", description = "医生登录获取JWT Token")
    public Result<DoctorLoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(doctorService.doctorLogin(dto));
    }

    @GetMapping("/my/registrations")
    @Operation(summary = "今日挂号", description = "获取当前医生今日待就诊的挂号记录")
    public Result<List<RegistrationVO>> myRegistrations(HttpServletRequest request) {
        Long doctorId = (Long) request.getAttribute("userId");
        return Result.success(registrationService.listByDoctorId(doctorId));
    }
}
