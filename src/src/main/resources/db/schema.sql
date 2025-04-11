create database AIservice;

use AIservice;
-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `avatar` VARCHAR(255) COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '用户角色：0-普通用户，1-VIP用户，2-HR管理员',
    `vip_expire_time` DATETIME COMMENT 'VIP过期时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账户状态：0-禁用，1-启用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` TINYINT NOT NULL COMMENT '订单类型：1-月度会员，2-季度会员，3-年度会员',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已取消，3-已退款',
    `pay_time` DATETIME COMMENT '支付时间',
    `pay_type` TINYINT COMMENT '支付方式：1-支付宝，2-微信，3-银行卡',
    `trade_no` VARCHAR(100) COMMENT '支付流水号',
    `remark` VARCHAR(255) COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 简历表
CREATE TABLE IF NOT EXISTS `resume` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '简历ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '简历名称',
    `file_url` VARCHAR(255) NOT NULL COMMENT '简历文件URL',
    `file_type` TINYINT NOT NULL COMMENT '文件类型：1-PDF，2-Word，3-其他',
    `parse_status` TINYINT NOT NULL DEFAULT 0 COMMENT '简历解析状态：0-未解析，1-解析中，2-解析成功，3-解析失败',
    `basic_info` TEXT COMMENT '基本信息（JSON格式）',
    `education` TEXT COMMENT '教育经历（JSON格式）',
    `work_experience` TEXT COMMENT '工作经历（JSON格式）',
    `project_experience` TEXT COMMENT '项目经历（JSON格式）',
    `skills` TEXT COMMENT '技能标签（JSON格式）',
    `score` INT COMMENT '简历评分',
    `suggestions` TEXT COMMENT '优化建议（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parse_status` (`parse_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 职位表
CREATE TABLE IF NOT EXISTS `job` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '职位ID',
    `title` VARCHAR(100) NOT NULL COMMENT '职位标题',
    `company` VARCHAR(100) NOT NULL COMMENT '公司名称',
    `location` VARCHAR(100) NOT NULL COMMENT '工作地点',
    `salary_min` DECIMAL(10,2) NOT NULL COMMENT '最低薪资（单位：K）',
    `salary_max` DECIMAL(10,2) NOT NULL COMMENT '最高薪资（单位：K）',
    `experience_years` INT COMMENT '工作经验要求（年）',
    `education` TINYINT COMMENT '学历要求：1-大专，2-本科，3-硕士，4-博士',
    `description` TEXT NOT NULL COMMENT '职位描述',
    `requirement` TEXT COMMENT '职位要求',
    `skills` TEXT COMMENT '技能要求（JSON格式）',
    `type` TINYINT NOT NULL COMMENT '职位类型：1-全职，2-兼职，3-实习',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '职位状态：0-已下线，1-招聘中',
    `source` TINYINT NOT NULL COMMENT '职位来源：1-BOSS直聘，2-拉勾，3-智联',
    `source_url` VARCHAR(255) COMMENT '原始职位链接',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_company` (`company`),
    KEY `idx_status` (`status`),
    KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='职位表';