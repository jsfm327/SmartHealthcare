package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.ScheduleDTO;
import com.healthware.service.ScheduleService;
import com.healthware.vo.ScheduleVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/list")
    public Result<PageResult<ScheduleVO>> list(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String date,
                                                @RequestParam(required = false) Long deptId) {
        return Result.success(scheduleService.listSchedules(page, size, date, deptId));
    }

    @GetMapping("/doctor/{doctorId}")
    public Result<List<ScheduleVO>> listByDoctor(@PathVariable Long doctorId) {
        return Result.success(scheduleService.listByDoctor(doctorId));
    }

    @GetMapping("/date/{date}")
    public Result<List<ScheduleVO>> listByDate(@PathVariable String date) {
        return Result.success(scheduleService.listByDate(date));
    }

    @PostMapping
    public Result<Void> add(@Valid @RequestBody ScheduleDTO dto) {
        scheduleService.addSchedule(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ScheduleDTO dto) {
        scheduleService.updateSchedule(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }
}
