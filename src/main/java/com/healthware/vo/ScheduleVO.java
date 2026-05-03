package com.healthware.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ScheduleVO {

    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long roomId;
    private String roomName;
    private Long departmentId;
    private String departmentName;
    private LocalDate scheduleDate;
    private Integer timeSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxAppointments;
    private Integer currentAppointments;
    private Integer status;
}
