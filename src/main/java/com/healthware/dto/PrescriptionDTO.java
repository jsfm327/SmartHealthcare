package com.healthware.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionDTO {

    @NotNull(message = "挂号ID不能为空")
    private Long registrationId;

    @NotNull(message = "医生ID不能为空")
    private Long doctorId;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    private String diagnosis;
    private String medicineList;
    private BigDecimal totalAmount;
    private String notes;
}
