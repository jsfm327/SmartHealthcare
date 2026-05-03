package com.healthware.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("patient")
public class Patient {

    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
