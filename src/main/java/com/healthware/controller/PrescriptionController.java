package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.dto.PrescriptionDTO;
import com.healthware.service.PrescriptionService;
import com.healthware.vo.PrescriptionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
@Tag(name = "处方管理", description = "处方开具与管理")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping
    @Operation(summary = "开具处方", description = "创建新的处方")
    public Result<Void> create(@Valid @RequestBody PrescriptionDTO dto) {
        prescriptionService.createPrescription(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "处方详情", description = "获取处方详细信息")
    public Result<PrescriptionVO> getDetail(
            @Parameter(description = "处方ID") @PathVariable Long id) {
        return Result.success(prescriptionService.getDetail(id));
    }

    @GetMapping("/registration/{regId}")
    @Operation(summary = "按挂号查询处方", description = "根据挂号ID获取处方")
    public Result<PrescriptionVO> getByRegistrationId(
            @Parameter(description = "挂号ID") @PathVariable Long regId) {
        return Result.success(prescriptionService.getByRegistrationId(regId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑处方", description = "更新处方信息")
    public Result<Void> update(@Parameter(description = "处方ID") @PathVariable Long id,
                               @Valid @RequestBody PrescriptionDTO dto) {
        prescriptionService.updatePrescription(id, dto);
        return Result.success();
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认发药", description = "确认处方已发药")
    public Result<Void> confirmDispense(
            @Parameter(description = "处方ID") @PathVariable Long id) {
        prescriptionService.confirmDispense(id);
        return Result.success();
    }
}
