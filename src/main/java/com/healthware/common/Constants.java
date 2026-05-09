package com.healthware.common;

public class Constants {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_HEADER = "Authorization";
    public static final long TOKEN_EXPIRATION = 7200000L; // 2小时

    public static final String USER_TOKEN_PREFIX = "user:token:";
    public static final String ADMIN_TOKEN_PREFIX = "admin:token:";
    public static final String DOCTOR_TOKEN_PREFIX = "doctor:token:";
    public static final String LOGIN_FAIL_PREFIX = "user:login:fail:";

    public static final int MAX_LOGIN_FAIL_COUNT = 5;
    public static final long LOGIN_LOCK_DURATION = 1800L; // 30分钟（秒）

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_DISABLED = 0;

    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    public static final int TIME_SLOT_MORNING = 1;
    public static final int TIME_SLOT_AFTERNOON = 2;

    public static final int REG_STATUS_PENDING = 0;
    public static final int REG_STATUS_VISITED = 1;
    public static final int REG_STATUS_CANCELLED = 2;
    public static final int REG_STATUS_EXPIRED = 3;

    private Constants() {}
}
