package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "病历请求参数")
public class MedicalRecordDTO {

    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", example = "1")
    private Long patientId;

    @NotNull(message = "医生ID不能为空")
    @Schema(description = "医生ID", example = "1")
    private Long doctorId;

    @Schema(description = "挂号ID", example = "1")
    private Long registrationId;

    @Schema(description = "主诉", example = "发热3天，咳嗽")
    private String chiefComplaint;

    @Schema(description = "现病史", example = "3天前开始发热，最高38.5℃")
    private String presentIllness;

    @Schema(description = "既往史", example = "无特殊病史")
    private String pastHistory;

    @Schema(description = "体格检查", example = "T:38.2℃, 咽部充血")
    private String physicalExam;

    @Schema(description = "诊断", example = "上呼吸道感染")
    private String diagnosis;

    @Schema(description = "治疗方案", example = "口服药物治疗")
    private String treatmentPlan;

    @Schema(description = "医嘱", example = "多饮水，注意休息")
    private String doctorAdvice;

    @NotNull(message = "就诊日期不能为空")
    @Schema(description = "就诊日期", example = "2026-05-04")
    private LocalDate visitDate;
}
