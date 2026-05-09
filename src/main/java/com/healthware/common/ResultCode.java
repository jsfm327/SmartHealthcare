package com.healthware.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_LOCKED(1003, "账号已被锁定，请30分钟后重试"),
    USERNAME_EXISTS(1004, "用户名已存在"),
    PHONE_EXISTS(1005, "手机号已被注册"),

    // 挂号相关 2xxx
    SCHEDULE_NOT_AVAILABLE(2001, "该排班不可用"),
    SCHEDULE_FULL(2002, "该时段已约满"),
    DUPLICATE_REGISTRATION(2003, "该患者当天已有挂号"),
    REGISTRATION_NOT_FOUND(2004, "挂号记录不存在"),
    REGISTRATION_CANCELLED(2005, "挂号已取消"),

    // 医生相关 1xxx
    DOCTOR_NOT_FOUND(1006, "医生账号不存在"),
    DOCTOR_PASSWORD_ERROR(1007, "医生密码错误"),
    DOCTOR_DISABLED(1008, "医生账号已被禁用"),

    // 业务相关 3xxx
    DEPARTMENT_NOT_FOUND(3001, "科室不存在"),
    PATIENT_NOT_FOUND(3003, "患者不存在"),
    CONSULT_FAILED(3004, "AI问诊失败，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
