package com.healthware.controller;

import com.healthware.common.PageResult;
import com.healthware.common.Result;
import com.healthware.dto.MedicalRecordDTO;
import com.healthware.service.MedicalRecordService;
import com.healthware.vo.MedicalRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-record")
@Tag(name = "病历管理", description = "病历记录的创建与管理")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    @Operation(summary = "创建病历", description = "创建新的病历记录")
    public Result<Void> create(@Valid @RequestBody MedicalRecordDTO dto) {
        medicalRecordService.createRecord(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "病历详情", description = "获取病历详细信息")
    public Result<MedicalRecordVO> getDetail(
            @Parameter(description = "病历ID") @PathVariable Long id) {
        return Result.success(medicalRecordService.getDetail(id));
    }

    @GetMapping("/list")
    @Operation(summary = "病历列表", description = "分页查询所有病历")
    public Result<PageResult<MedicalRecordVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(medicalRecordService.listAll(page, size));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "按患者查询病历", description = "获取指定患者的所有病历")
    public Result<List<MedicalRecordVO>> listByPatient(
            @Parameter(description = "患者ID") @PathVariable Long patientId) {
        return Result.success(medicalRecordService.listByPatient(patientId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新病历", description = "更新病历信息")
    public Result<Void> update(
            @Parameter(description = "病历ID") @PathVariable Long id,
            @Valid @RequestBody MedicalRecordDTO dto) {
        medicalRecordService.updateRecord(id, dto);
        return Result.success();
    }
}
