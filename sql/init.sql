-- ============================================
-- 智能医疗系统 - 数据库初始化脚本
-- 数据库: healthware
-- 字符集: utf8mb4
-- ============================================

CREATE DATABASE IF NOT EXISTS healthware DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE healthware;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5加密）',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `id_card` VARCHAR(18) UNIQUE COMMENT '身份证号',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0锁定 1正常',
    `login_fail_count` INT DEFAULT 0 COMMENT '连续登录失败次数',
    `lock_time` DATETIME COMMENT '锁定时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 管理员表
-- ============================================
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5加密）',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `role` TINYINT DEFAULT 1 COMMENT '角色：1普通管理员 2超级管理员',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- ============================================
-- 3. 科室表
-- ============================================
CREATE TABLE IF NOT EXISTS `department` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '科室ID',
    `name` VARCHAR(50) NOT NULL UNIQUE COMMENT '科室名称',
    `description` TEXT COMMENT '科室描述',
    `location` VARCHAR(100) COMMENT '科室位置',
    `phone` VARCHAR(20) COMMENT '科室电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0停诊 1正常',
    `sort_order` INT DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

-- ============================================
-- 4. 医生表
-- ============================================
CREATE TABLE IF NOT EXISTS `doctor` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '医生ID',
    `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `department_id` BIGINT NOT NULL COMMENT '所属科室ID',
    `title` VARCHAR(50) COMMENT '职称',
    `specialty` VARCHAR(255) COMMENT '擅长领域',
    `introduction` TEXT COMMENT '个人简介',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0停诊 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- ============================================
-- 5. 诊室表
-- ============================================
CREATE TABLE IF NOT EXISTS `room` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '诊室ID',
    `name` VARCHAR(50) NOT NULL COMMENT '诊室名称',
    `department_id` BIGINT COMMENT '所属科室ID',
    `location` VARCHAR(100) COMMENT '诊室位置',
    `capacity` INT DEFAULT 1 COMMENT '每时段最大接诊量',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0停用 1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊室表';

-- ============================================
-- 6. 排班时段表
-- ============================================
CREATE TABLE IF NOT EXISTS `schedule` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '排班ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `room_id` BIGINT NOT NULL COMMENT '诊室ID',
    `schedule_date` DATE NOT NULL COMMENT '排班日期',
    `time_slot` TINYINT NOT NULL COMMENT '时段：1上午 2下午',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `max_appointments` INT DEFAULT 15 COMMENT '最大预约数',
    `current_appointments` INT DEFAULT 0 COMMENT '当前已预约数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0停诊 1正常 2已满',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_doctor_date_slot` (`doctor_id`, `schedule_date`, `time_slot`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班时段表';

-- ============================================
-- 7. 患者信息表
-- ============================================
CREATE TABLE IF NOT EXISTS `patient` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '患者ID',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
    `gender` TINYINT DEFAULT 0 COMMENT '性别',
    `age` INT COMMENT '年龄',
    `id_card` VARCHAR(18) COMMENT '身份证号',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `address` VARCHAR(255) COMMENT '地址',
    `medical_history` TEXT COMMENT '病史',
    `allergy_history` TEXT COMMENT '过敏史',
    `relationship` VARCHAR(20) COMMENT '与用户关系',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- ============================================
-- 8. 挂号记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `registration` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '挂号ID',
    `registration_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '挂号单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `department_id` BIGINT NOT NULL COMMENT '科室ID',
    `room_id` BIGINT COMMENT '诊室ID',
    `schedule_id` BIGINT NOT NULL COMMENT '排班ID',
    `registration_date` DATE NOT NULL COMMENT '就诊日期',
    `time_slot` TINYINT NOT NULL COMMENT '时段：1上午 2下午',
    `queue_number` INT COMMENT '排队序号',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待就诊 1已就诊 2已取消 3已过期',
    `cancel_reason` VARCHAR(255) COMMENT '取消原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`department_id`) REFERENCES `department`(`id`),
    FOREIGN KEY (`room_id`) REFERENCES `room`(`id`),
    FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号记录表';

-- ============================================
-- 9. AI问诊记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `consult_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) COMMENT '问诊标题',
    `symptoms` TEXT COMMENT '症状描述',
    `ai_response` TEXT COMMENT 'AI回复内容',
    `department_suggest` VARCHAR(50) COMMENT '建议就诊科室',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0已结束 1进行中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问诊记录表';

-- ============================================
-- 10. 用户反馈表
-- ============================================
CREATE TABLE IF NOT EXISTS `feedback` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
    `user_id` BIGINT COMMENT '反馈用户ID',
    `title` VARCHAR(100) COMMENT '反馈标题',
    `content` TEXT NOT NULL COMMENT '反馈内容',
    `contact` VARCHAR(50) COMMENT '联系方式',
    `reply_content` TEXT COMMENT '管理员回复',
    `reply_time` DATETIME COMMENT '回复时间',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0待处理 1已回复 2已关闭',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- ============================================
-- 11. 处方记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `prescription` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '处方ID',
    `registration_id` BIGINT NOT NULL COMMENT '关联挂号ID',
    `doctor_id` BIGINT NOT NULL COMMENT '开方医生ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `diagnosis` VARCHAR(255) COMMENT '诊断结果',
    `medicine_list` TEXT COMMENT '药品清单（JSON格式）',
    `total_amount` DECIMAL(10,2) COMMENT '总金额',
    `notes` TEXT COMMENT '医嘱备注',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0未取药 1已取药',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`registration_id`) REFERENCES `registration`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方记录表';

-- ============================================
-- 12. 病历记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `medical_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '病历ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `doctor_id` BIGINT NOT NULL COMMENT '接诊医生ID',
    `registration_id` BIGINT COMMENT '关联挂号ID',
    `chief_complaint` TEXT COMMENT '主诉',
    `present_illness` TEXT COMMENT '现病史',
    `past_history` TEXT COMMENT '既往史',
    `physical_exam` TEXT COMMENT '体格检查',
    `diagnosis` VARCHAR(255) COMMENT '诊断',
    `treatment_plan` TEXT COMMENT '治疗方案',
    `doctor_advice` TEXT COMMENT '医生建议',
    `visit_date` DATE NOT NULL COMMENT '就诊日期',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`patient_id`) REFERENCES `patient`(`id`),
    FOREIGN KEY (`doctor_id`) REFERENCES `doctor`(`id`),
    FOREIGN KEY (`registration_id`) REFERENCES `registration`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历记录表';

-- ============================================
-- 插入默认管理员账号
-- 密码: admin123 (MD5加密)
-- ============================================
INSERT INTO `admin` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '超级管理员', 2, 1);
