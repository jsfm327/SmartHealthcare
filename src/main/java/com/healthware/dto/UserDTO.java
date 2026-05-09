package com.healthware.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息更新参数")
public class UserDTO {

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;

    @Schema(description = "性别: 0-未知, 1-男, 2-女", example = "1")
    private Integer gender;

    @Schema(description = "头像URL")
    private String avatar;
}
