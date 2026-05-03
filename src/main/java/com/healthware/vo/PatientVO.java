package com.healthware.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientVO {

    private Long id;
    private Long userId;
    private String name;
    private Integer gender;
    private Integer age;
    private String idCard;
    private String phone;
    private String address;
    private String medicalHistory;
    private String allergyHistory;
    private String relationship;
    private LocalDateTime createTime;
}
