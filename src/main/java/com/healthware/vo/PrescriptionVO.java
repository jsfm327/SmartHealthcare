package com.healthware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PrescriptionVO {

    private Long id;
    private Long registrationId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private String diagnosis;
    private String medicineList;
    private BigDecimal totalAmount;
    private String notes;
    private Integer status;
    private LocalDateTime createTime;
}
