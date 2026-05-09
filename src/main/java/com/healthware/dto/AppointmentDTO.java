package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "预约挂号请求参数")
public class AppointmentDTO {

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", example = "1")
    private Long patientId;

    @NotNull(message = "排班ID不能为空")
    @Schema(description = "排班ID", example = "1")
    private Long scheduleId;
}
