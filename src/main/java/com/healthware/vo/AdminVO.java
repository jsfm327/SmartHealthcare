package com.healthware.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminVO {

    private Long id;
    private String username;
    private String realName;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
}
