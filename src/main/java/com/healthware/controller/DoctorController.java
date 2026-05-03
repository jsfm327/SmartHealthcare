package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.entity.Doctor;
import com.healthware.service.DoctorService;
import com.healthware.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/list")
    public Result<PageResult<DoctorVO>> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(required = false) Long deptId) {
        return Result.success(doctorService.listDoctors(page, size, deptId));
    }

    @GetMapping("/{id}")
    public Result<DoctorVO> getDetail(@PathVariable Long id) {
        return Result.success(doctorService.getDetail(id));
    }

    @GetMapping("/department/{deptId}")
    public Result<List<DoctorVO>> listByDepartment(@PathVariable Long deptId) {
        return Result.success(doctorService.listByDepartment(deptId));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Doctor doctor) {
        doctorService.addDoctor(doctor);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Doctor doctor) {
        doctorService.updateDoctor(id, doctor);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Result.success();
    }
}
