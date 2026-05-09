package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "排班请求参数")
public class ScheduleDTO {

    @NotNull(message = "医生ID不能为空")
    @Schema(description = "医生ID", example = "1")
    private Long doctorId;

    @NotNull(message = "排班日期不能为空")
    @Schema(description = "排班日期", example = "2026-05-10")
    private LocalDate scheduleDate;

    @NotNull(message = "时段不能为空")
    @Schema(description = "时段: 1-上午, 2-下午", example = "1")
    private Integer timeSlot;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "08:00")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "12:00")
    private LocalTime endTime;

    @Schema(description = "最大预约数", example = "15")
    private Integer maxAppointments = 15;
}
