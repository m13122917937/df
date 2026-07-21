CREATE TABLE IF NOT EXISTS `ana_warehouse_cost_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `month_value` char(7) NOT NULL COMMENT '月份',
  `after_sales_headcount` decimal(18,2) NOT NULL COMMENT '售后人力',
  `after_sales_labor_cost` decimal(18,2) NOT NULL COMMENT '售后人力成本',
  `created_by` bigint DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint DEFAULT NULL,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_warehouse_cost_month` (`month_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析仓储售后成本配置';
