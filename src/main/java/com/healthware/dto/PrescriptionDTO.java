package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "处方请求参数")
public class PrescriptionDTO {

    @NotNull(message = "挂号ID不能为空")
    @Schema(description = "挂号ID", example = "1")
    private Long registrationId;

    @NotNull(message = "医生ID不能为空")
    @Schema(description = "医生ID", example = "1")
    private Long doctorId;

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", example = "1")
    private Long patientId;

    @Schema(description = "药品列表", example = "阿莫西林胶囊 x2, 布洛芬片 x1")
    private String medicineList;

    @Schema(description = "总金额", example = "128.50")
    private BigDecimal totalAmount;

    @Schema(description = "备注", example = "饭后服用")
    private String notes;
}
