package com.healthware.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String realName;
    private String phone;
    private String email;
    private String idCard;
    private Integer gender;
    private String avatar;
}
