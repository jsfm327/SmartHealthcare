package com.healthware.vo;

import lombok.Data;

@Data
public class RoomVO {

    private Long id;
    private String name;
    private Long departmentId;
    private String departmentName;
    private String location;
    private Integer capacity;
    private Integer status;
}
