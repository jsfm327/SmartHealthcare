package com.healthware.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String registrationNo;
    private Long userId;
    private Long patientId;
    private Long doctorId;
    private Long departmentId;
    private Long scheduleId;
    private LocalDate registrationDate;
    private Integer timeSlot;
    private Integer queueNumber;
    private Integer status;
    private String cancelReason;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
