package com.healthware.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consult_record")
public class ConsultRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String symptoms;
    private String aiResponse;
    private String departmentSuggest;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
