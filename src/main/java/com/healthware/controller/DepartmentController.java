package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.entity.Department;
import com.healthware.service.DepartmentService;
import com.healthware.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    public Result<List<DepartmentVO>> list(@RequestParam(required = false) Integer page,
                                           @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return Result.success(departmentService.listPage(page, size).getRecords());
        }
        return Result.success(departmentService.listAll());
    }

    @GetMapping("/{id}")
    public Result<DepartmentVO> getDetail(@PathVariable Long id) {
        return Result.success(departmentService.getDetail(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Department dept) {
        departmentService.addDepartment(dept);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Department dept) {
        departmentService.updateDepartment(id, dept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success();
    }
}
