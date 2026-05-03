package com.healthware.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentDTO {

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;

    @NotNull(message = "科室ID不能为空")
    private Long departmentId;
}
