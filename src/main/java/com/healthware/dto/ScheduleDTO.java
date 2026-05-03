package com.healthware.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleDTO {

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "诊室ID不能为空")
    private Long roomId;

    @NotNull(message = "排班日期不能为空")
    private LocalDate scheduleDate;

    @NotNull(message = "时段不能为空")
    private Integer timeSlot;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalTime endTime;

    private Integer maxAppointments = 15;
}
