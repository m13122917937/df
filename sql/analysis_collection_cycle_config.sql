-- 回款周期独立表：不修改 ana_cost_config 或其他既有业务表。
CREATE TABLE IF NOT EXISTS `ana_collection_cycle_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(64) NOT NULL COMMENT '平台',
  `shop_name` varchar(128) NOT NULL COMMENT '店铺名称',
  `goods_collection_days` int NOT NULL COMMENT '货款回款天数',
  `subsidy_collection_days` int NOT NULL COMMENT '补贴款回款天数',
  `national_subsidy_collection_days` int NOT NULL COMMENT '国补款回款天数',
  `created_by` bigint DEFAULT NULL COMMENT '创建人',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` bigint DEFAULT NULL COMMENT '更新人',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_cycle_platform_shop` (`platform`, `shop_name`),
  KEY `idx_ana_cycle_updated_time` (`updated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析回款周期配置';
