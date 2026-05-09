package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "就诊人请求参数")
public class PatientDTO {

    @NotBlank(message = "患者姓名不能为空")
    @Schema(description = "患者姓名", example = "张三")
    private String name;

    @Schema(description = "性别: 0-未知, 1-男, 2-女", example = "1")
    private Integer gender;

    @Schema(description = "年龄", example = "30")
    private Integer age;

    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "地址", example = "北京市朝阳区")
    private String address;

    @Schema(description = "病史")
    private String medicalHistory;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @NotBlank(message = "与用户关系不能为空")
    @Schema(description = "与用户关系", example = "本人")
    private String relationship;
}
