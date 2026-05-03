package com.healthware.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientDTO {

    @NotBlank(message = "患者姓名不能为空")
    private String name;

    private Integer gender;
    private Integer age;
    private String idCard;
    private String phone;
    private String address;
    private String medicalHistory;
    private String allergyHistory;

    @NotBlank(message = "与用户关系不能为空")
    private String relationship;
}
