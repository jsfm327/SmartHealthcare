package com.healthware.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultVO {

    private Long id;
    private String title;
    private String symptoms;
    private String analysis;
    private String advice;
    private String departmentSuggest;
    private Integer status;
    private LocalDateTime createTime;
}
