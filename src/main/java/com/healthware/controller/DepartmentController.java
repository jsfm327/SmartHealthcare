package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.entity.Department;
import com.healthware.service.DepartmentService;
import com.healthware.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
@Tag(name = "科室管理", description = "科室信息的增删改查")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    @Operation(summary = "科室列表", description = "获取所有科室列表，支持分页")
    public Result<List<DepartmentVO>> list(
            @Parameter(description = "页码") @RequestParam(required = false) Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return Result.success(departmentService.listPage(page, size).getRecords());
        }
        return Result.success(departmentService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "科室详情", description = "获取科室详细信息")
    public Result<DepartmentVO> getDetail(
            @Parameter(description = "科室ID") @PathVariable Long id) {
        return Result.success(departmentService.getDetail(id));
    }

    @PostMapping
    @Operation(summary = "添加科室", description = "添加新的科室")
    public Result<Void> add(@RequestBody Department dept) {
        departmentService.addDepartment(dept);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新科室", description = "更新科室信息")
    public Result<Void> update(
            @Parameter(description = "科室ID") @PathVariable Long id,
            @RequestBody Department dept) {
        departmentService.updateDepartment(id, dept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除科室", description = "删除指定科室")
    public Result<Void> delete(
            @Parameter(description = "科室ID") @PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}
