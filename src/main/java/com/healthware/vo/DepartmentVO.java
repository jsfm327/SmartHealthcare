package com.healthware.vo;

import lombok.Data;

@Data
public class DepartmentVO {

    private Long id;
    private String name;
    private String description;
    private String location;
    private String phone;
    private Integer status;
    private Integer sortOrder;
    private Integer doctorCount;
}
