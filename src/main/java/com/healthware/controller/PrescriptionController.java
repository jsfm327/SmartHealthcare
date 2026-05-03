package com.healthware.controller;

import com.healthware.common.Result;
import com.healthware.dto.PrescriptionDTO;
import com.healthware.service.PrescriptionService;
import com.healthware.vo.PrescriptionVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping
    public Result<Void> create(@Valid @RequestBody PrescriptionDTO dto) {
        prescriptionService.createPrescription(dto);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<PrescriptionVO> getDetail(@PathVariable Long id) {
        return Result.success(prescriptionService.getDetail(id));
    }

    @GetMapping("/registration/{regId}")
    public Result<PrescriptionVO> getByRegistrationId(@PathVariable Long regId) {
        return Result.success(prescriptionService.getByRegistrationId(regId));
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirmDispense(@PathVariable Long id) {
        prescriptionService.confirmDispense(id);
        return Result.success();
    }
}
