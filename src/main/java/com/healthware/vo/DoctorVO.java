package com.healthware.vo;

import lombok.Data;

@Data
public class DoctorVO {

    private Long id;
    private String name;
    private Integer gender;
    private Long departmentId;
    private String departmentName;
    private String title;
    private String specialty;
    private String introduction;
    private String avatar;
    private String phone;
    private Integer status;
}
