-- 平台服务费费率改造：新建费率表 + 清理旧数据
-- 执行日期: 2026-07-27

-- 1. 清理旧 PLATFORM_FEE 数据
DELETE FROM ana_cost_config WHERE config_type = 'PLATFORM_FEE';

-- 2. 创建平台服务费率表
CREATE TABLE IF NOT EXISTS `ana_platform_fee_rate` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT COMMENT '主键',
    `platform`      VARCHAR(64)     NOT NULL DEFAULT ''     COMMENT '平台代码（来源：m_sales_channel）',
    `business_type` TINYINT(4)      DEFAULT NULL            COMMENT '业态，null=通配',
    `category`      VARCHAR(128)    DEFAULT NULL            COMMENT '品类，null=通配',
    `fee_rate`      DECIMAL(10,6)   NOT NULL DEFAULT 0      COMMENT '费率(%), 如2.500000=2.5%',
    `remark`        VARCHAR(500)    DEFAULT NULL            COMMENT '备注',
    `created_by`    BIGINT          DEFAULT NULL            COMMENT '创建人',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by`    BIGINT          DEFAULT NULL            COMMENT '更新人',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dimensions` (`platform`, `business_type`, `category`),
    KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='平台服务费率配置';
