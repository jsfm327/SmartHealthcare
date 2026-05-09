package com.healthware.vo;

import lombok.Data;

@Data
public class DoctorLoginVO {

    private String token;
    private Long doctorId;
    private String username;
    private String realName;
}
