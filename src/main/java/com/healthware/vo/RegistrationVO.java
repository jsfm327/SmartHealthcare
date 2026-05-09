package com.healthware.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegistrationVO {

    private Long id;
    private String registrationNo;
    private Long userId;
    private String userName;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long departmentId;
    private String departmentName;
    private LocalDate registrationDate;
    private Integer timeSlot;
    private Integer queueNumber;
    private Integer status;
    private String cancelReason;
    private LocalDateTime createTime;
}
