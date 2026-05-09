package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.ScheduleDTO;
import com.healthware.service.ScheduleService;
import com.healthware.vo.ScheduleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "排班管理", description = "医生排班信息的增删改查")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/list")
    @Operation(summary = "排班列表", description = "分页获取排班列表，可按日期和科室筛选")
    public Result<PageResult<ScheduleVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "日期") @RequestParam(required = false) String date,
            @Parameter(description = "科室ID") @RequestParam(required = false) Long deptId) {
        return Result.success(scheduleService.listSchedules(page, size, date, deptId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "排班详情", description = "获取排班详细信息")
    public Result<ScheduleVO> getDetail(
            @Parameter(description = "排班ID") @PathVariable Long id) {
        return Result.success(scheduleService.getDetail(id));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "按医生查询排班", description = "获取指定医生的所有排班")
    public Result<List<ScheduleVO>> listByDoctor(
            @Parameter(description = "医生ID") @PathVariable Long doctorId) {
        return Result.success(scheduleService.listByDoctor(doctorId));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "按日期查询排班", description = "获取指定日期的所有排班")
    public Result<List<ScheduleVO>> listByDate(
            @Parameter(description = "日期，格式：yyyy-MM-dd") @PathVariable String date) {
        return Result.success(scheduleService.listByDate(date));
    }

    @PostMapping
    @Operation(summary = "添加排班", description = "添加新的排班信息")
    public Result<Void> add(@Valid @RequestBody ScheduleDTO dto) {
        scheduleService.addSchedule(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新排班", description = "更新排班信息")
    public Result<Void> update(
            @Parameter(description = "排班ID") @PathVariable Long id,
            @Valid @RequestBody ScheduleDTO dto) {
        scheduleService.updateSchedule(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除排班", description = "删除指定排班")
    public Result<Void> delete(
            @Parameter(description = "排班ID") @PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }
}
