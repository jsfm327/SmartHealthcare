package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.PatientDTO;
import com.healthware.service.PatientService;
import com.healthware.vo.PatientVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/my")
    public Result<List<PatientVO>> myPatients(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(patientService.myPatients(userId));
    }

    @PostMapping
    public Result<Void> add(HttpServletRequest request, @Valid @RequestBody PatientDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        patientService.addPatient(userId, dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PatientDTO dto) {
        patientService.updatePatient(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        patientService.deletePatient(id, userId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<PatientVO>> listAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(patientService.listAll(page, size));
    }
}
