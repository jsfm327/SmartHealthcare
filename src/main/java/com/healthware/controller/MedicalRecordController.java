package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.dto.MedicalRecordDTO;
import com.healthware.service.MedicalRecordService;
import com.healthware.vo.MedicalRecordVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody MedicalRecordDTO dto) {
        medicalRecordService.createRecord(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<MedicalRecordVO> getDetail(@PathVariable Long id) {
        return Result.success(medicalRecordService.getDetail(id));
    }

    @GetMapping("/patient/{patientId}")
    public Result<List<MedicalRecordVO>> listByPatient(@PathVariable Long patientId) {
        return Result.success(medicalRecordService.listByPatient(patientId));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MedicalRecordDTO dto) {
        medicalRecordService.updateRecord(id, dto);
        return Result.success();
    }
}
