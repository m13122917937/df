-- 保证金配置独立表：仅保存平台、店铺、保证金及操作审计字段。
-- 不修改 ana_cost_config 或任何既有业务表。
CREATE TABLE IF NOT EXISTS `ana_margin_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(64) NOT NULL COMMENT '平台',
  `shop_name` varchar(128) NOT NULL COMMENT '店铺名称',
  `margin_amount` decimal(18,2) NOT NULL COMMENT '保证金金额',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_margin_platform_shop` (`platform`, `shop_name`),
  KEY `idx_ana_margin_updated_time` (`updated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析保证金配置';
