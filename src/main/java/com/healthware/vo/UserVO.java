package com.healthware.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String idCard;
    private Integer gender;
    private String avatar;
    private Integer status;
    private LocalDateTime createTime;
}
