-- 运营管理经营分析模块（仅创建新表，不回填历史数据）

CREATE TABLE IF NOT EXISTS `ana_order_fact` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `fact_key` varchar(180) NOT NULL COMMENT '吉客云交易单号+商品行唯一键',
  `jky_trade_no` varchar(64) NOT NULL COMMENT '吉客云交易单号',
  `jky_order_no` varchar(64) DEFAULT NULL COMMENT '吉客云系统订单号',
  `original_order_no` varchar(128) DEFAULT NULL COMMENT '原始订单号',
  `goods_line_key` varchar(128) NOT NULL COMMENT '商品行唯一标识',
  `business_date` date NOT NULL COMMENT '经营日期（发货日期）',
  `trade_time` datetime DEFAULT NULL COMMENT '成交时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `source_modified_time` datetime DEFAULT NULL COMMENT '来源最后修改时间',
  `subject_name` varchar(128) DEFAULT NULL COMMENT '经营主体',
  `platform` varchar(64) DEFAULT NULL COMMENT '平台',
  `shop_id` varchar(64) DEFAULT NULL COMMENT '店铺ID',
  `shop_name` varchar(128) DEFAULT NULL COMMENT '店铺',
  `warehouse_id` varchar(64) DEFAULT NULL COMMENT '仓库ID',
  `warehouse_name` varchar(128) DEFAULT NULL COMMENT '仓库',
  `supplier_name` varchar(128) DEFAULT NULL COMMENT '供应商',
  `goods_id` varchar(64) DEFAULT NULL COMMENT '商品ID',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '货品编码',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `spec_name` varchar(255) DEFAULT NULL COMMENT '规格',
  `brand` varchar(128) DEFAULT NULL COMMENT '品牌',
  `category` varchar(128) DEFAULT NULL COMMENT '品类',
  `quantity` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '数量',
  `unit_price` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '销售单价',
  `goods_amount` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '商品金额',
  `payment_amount` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '实付分摊金额',
  `discount_amount` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '优惠分摊金额',
  `platform_subsidy` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '平台补贴',
  `government_subsidy` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '政府补贴',
  `order_expense` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '订单费用分摊',
  `assessment_cost` decimal(18,4) DEFAULT NULL COMMENT '吉客云评估单位成本',
  `goods_cost` decimal(18,4) DEFAULT NULL COMMENT '商品成本合计',
  `goods_incentive` decimal(18,4) NOT NULL DEFAULT 0 COMMENT '商品激励',
  `goods_gross_profit` decimal(18,4) DEFAULT NULL COMMENT '商品毛利',
  `fulfillment_gross_profit` decimal(18,4) DEFAULT NULL COMMENT '履约毛利',
  `department_profit` decimal(18,4) DEFAULT NULL COMMENT '部门利润',
  `operating_profit` decimal(18,4) DEFAULT NULL COMMENT '经营利润',
  `order_type` varchar(32) NOT NULL DEFAULT 'NORMAL' COMMENT '订单类型',
  `order_status` varchar(32) DEFAULT NULL COMMENT '订单状态',
  `gift_flag` tinyint NOT NULL DEFAULT 0 COMMENT '是否赠品',
  `calc_status` varchar(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/COMPLETE/INCOMPLETE',
  `missing_reason` varchar(500) DEFAULT NULL COMMENT '缺失原因',
  `raw_hash` varchar(64) DEFAULT NULL COMMENT '原始数据摘要',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_order_fact_key` (`fact_key`),
  KEY `idx_ana_fact_date` (`business_date`),
  KEY `idx_ana_fact_dimension` (`business_date`,`platform`,`shop_name`,`brand`,`category`),
  KEY `idx_ana_fact_original` (`original_order_no`,`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析订单商品行事实表';

CREATE TABLE IF NOT EXISTS `ana_refund_fact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `refund_key` varchar(180) NOT NULL COMMENT '退款单号+退款商品行',
  `refund_no` varchar(64) NOT NULL,
  `original_order_no` varchar(128) DEFAULT NULL,
  `goods_line_key` varchar(128) NOT NULL,
  `goods_no` varchar(64) DEFAULT NULL,
  `goods_name` varchar(255) DEFAULT NULL,
  `refund_success_time` datetime NOT NULL,
  `refund_date` date NOT NULL,
  `refund_amount` decimal(18,4) NOT NULL DEFAULT 0,
  `platform_refund_amount` decimal(18,4) NOT NULL DEFAULT 0,
  `refund_quantity` decimal(18,4) NOT NULL DEFAULT 0,
  `has_goods_return` tinyint NOT NULL DEFAULT 0,
  `reversed_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `match_status` varchar(16) NOT NULL DEFAULT 'PENDING',
  `exception_reason` varchar(500) DEFAULT NULL,
  `raw_hash` varchar(64) DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_refund_key` (`refund_key`),
  KEY `idx_ana_refund_date` (`refund_date`),
  KEY `idx_ana_refund_order` (`original_order_no`,`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析退款事实表';

CREATE TABLE IF NOT EXISTS `ana_sync_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sync_type` varchar(32) NOT NULL,
  `window_start` datetime NOT NULL,
  `window_end` datetime NOT NULL,
  `status` varchar(16) NOT NULL,
  `read_count` int NOT NULL DEFAULT 0,
  `insert_count` int NOT NULL DEFAULT 0,
  `update_count` int NOT NULL DEFAULT 0,
  `skip_count` int NOT NULL DEFAULT 0,
  `error_message` text,
  `started_time` datetime NOT NULL,
  `finished_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ana_sync_window` (`sync_type`,`window_start`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析同步日志';

CREATE TABLE IF NOT EXISTS `ana_cost_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_type` varchar(32) NOT NULL COMMENT 'REBATE/FIXED_COEFFICIENT/CASHBACK/PENALTY/PROMOTION/MARGIN/COLLECTION_DAYS/INTERNAL_COST/WAREHOUSE_COST/SHOP_WHITELIST',
  `business_date` date DEFAULT NULL,
  `month_value` char(7) DEFAULT NULL,
  `platform` varchar(64) DEFAULT NULL,
  `shop_name` varchar(128) DEFAULT NULL,
  `original_order_no` varchar(128) DEFAULT NULL,
  `goods_no` varchar(64) DEFAULT NULL,
  `goods_name` varchar(255) DEFAULT NULL,
  `brand` varchar(128) DEFAULT NULL,
  `category` varchar(128) DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `coefficient` decimal(18,8) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `extra_data` text COMMENT '扩展配置JSON',
  `created_by` bigint DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint DEFAULT NULL,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ana_config_type_date` (`config_type`,`business_date`),
  KEY `idx_ana_config_dimension` (`config_type`,`platform`,`shop_name`,`goods_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经营分析通用核算配置';

CREATE TABLE IF NOT EXISTS `ana_rebate_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_code` varchar(64) NOT NULL,
  `activity_name` varchar(255) NOT NULL,
  `brand` varchar(128) DEFAULT NULL,
  `supplier_name` varchar(128) DEFAULT NULL,
  `scene` varchar(32) NOT NULL,
  `granularity` varchar(32) NOT NULL,
  `calculation_method` varchar(32) NOT NULL,
  `calculation_time` varchar(32) NOT NULL,
  `total_amount` decimal(18,4) DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` varchar(16) NOT NULL DEFAULT 'PENDING',
  `attachment_data` text,
  `created_by` bigint DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint DEFAULT NULL,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_rebate_code` (`activity_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='返利价保活动';

CREATE TABLE IF NOT EXISTS `ana_rebate_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `activity_id` bigint NOT NULL,
  `goods_no` varchar(64) DEFAULT NULL,
  `goods_name` varchar(255) DEFAULT NULL,
  `sn_code` varchar(128) DEFAULT NULL,
  `amount` decimal(18,4) DEFAULT NULL,
  `point_rate` decimal(18,8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ana_rebate_activity` (`activity_id`),
  KEY `idx_ana_rebate_match` (`goods_no`,`sn_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='返利价保活动明细';

CREATE TABLE IF NOT EXISTS `ana_daily_metric` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `metric_date` date NOT NULL,
  `subject_name` varchar(128) NOT NULL DEFAULT '',
  `platform` varchar(64) NOT NULL DEFAULT '',
  `shop_name` varchar(128) NOT NULL DEFAULT '',
  `brand` varchar(128) NOT NULL DEFAULT '',
  `category` varchar(128) NOT NULL DEFAULT '',
  `goods_no` varchar(64) NOT NULL DEFAULT '',
  `order_type` varchar(32) NOT NULL DEFAULT 'NORMAL',
  `sales_quantity` decimal(18,4) NOT NULL DEFAULT 0,
  `goods_income` decimal(18,4) NOT NULL DEFAULT 0,
  `platform_subsidy` decimal(18,4) NOT NULL DEFAULT 0,
  `government_subsidy` decimal(18,4) NOT NULL DEFAULT 0,
  `sales_revenue` decimal(18,4) NOT NULL DEFAULT 0,
  `goods_cost` decimal(18,4) DEFAULT NULL,
  `goods_incentive` decimal(18,4) NOT NULL DEFAULT 0,
  `goods_gross_profit` decimal(18,4) DEFAULT NULL,
  `platform_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `logistics_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `marketing_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `impairment_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `penalty_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `tax_fee` decimal(18,4) NOT NULL DEFAULT 0,
  `fulfillment_gross_profit` decimal(18,4) DEFAULT NULL,
  `capital_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `direct_labor_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `department_direct_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `other_adjustment` decimal(18,4) NOT NULL DEFAULT 0,
  `department_profit` decimal(18,4) DEFAULT NULL,
  `indirect_labor_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `allocated_indirect_cost` decimal(18,4) NOT NULL DEFAULT 0,
  `operating_profit` decimal(18,4) DEFAULT NULL,
  `fact_count` int NOT NULL DEFAULT 0,
  `incomplete_count` int NOT NULL DEFAULT 0,
  `calc_status` varchar(16) NOT NULL DEFAULT 'COMPLETE',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ana_daily_dimension` (`metric_date`,`subject_name`,`platform`,`shop_name`,`brand`,`category`,`goods_no`,`order_type`),
  KEY `idx_ana_metric_date` (`metric_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日经营指标快照';

-- 经营分析业务数据只写 ana_* 新表；以下仅注册若依菜单和任务元数据。
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '运营管理', 0, 65, 'operationsManage', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '运营管理目录'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'operationsManage' AND parent_id = 0);
SET @analysis_root = (SELECT menu_id FROM sys_menu WHERE path = 'operationsManage' AND parent_id = 0 LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '数据看板', @analysis_root, 1, 'databoard', NULL, 1, 0, 'M', '0', '0', '', 'dashboard', 'admin', NOW(), '经营分析数据看板'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'databoard' AND parent_id = @analysis_root);
SET @analysis_board = (SELECT menu_id FROM sys_menu WHERE path = 'databoard' AND parent_id = @analysis_root LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营统计', @analysis_board, 1, 'operationStats', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'chart', 'admin', NOW(), '经营统计看板'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'operationStats' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '绩效汇总', @analysis_board, 2, 'performanceRollup', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'table', 'admin', NOW(), '经营绩效汇总'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'performanceRollup' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '产渠分析', @analysis_board, 3, 'channelProduction', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'tree', 'admin', NOW(), '品牌品类及渠道分析'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'channelProduction' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '人效分析', @analysis_board, 4, 'humanEfficiency', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'people', 'admin', NOW(), '经营人效分析'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'humanEfficiency' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '指标树', @analysis_board, 5, 'metricTree', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'tree-table', 'admin', NOW(), '利润指标树'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'metricTree' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '订单穿透', @analysis_board, 6, 'orderDetails', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'list', 'admin', NOW(), '经营订单明细'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'orderDetails' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '数据质量', @analysis_board, 7, 'dataQuality', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'bug', 'admin', NOW(), '经营数据质量'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'dataQuality' AND parent_id = @analysis_board);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '同步日志', @analysis_board, 8, 'syncLogs', 'operationsManage/syncLogs/index', 1, 0, 'C', '0', '0', 'analysis:sync:logs', 'time-range', 'admin', NOW(), '经营数据同步日志'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'syncLogs' AND parent_id = @analysis_board);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '核算配置', @analysis_root, 2, 'accounting', NULL, 1, 0, 'M', '0', '0', '', 'edit', 'admin', NOW(), '经营核算配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'accounting' AND parent_id = @analysis_root);
SET @analysis_config = (SELECT menu_id FROM sys_menu WHERE path = 'accounting' AND parent_id = @analysis_root LIMIT 1);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '返利价保', @analysis_config, 1, 'rebateProtection', 'operationsManage/rebate/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'money', 'admin', NOW(), '返利价保配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'rebateProtection' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '固定系数', @analysis_config, 2, 'fixedCoefficient', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'skill', 'admin', NOW(), '固定系数配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'fixedCoefficient' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '返现', @analysis_config, 3, 'cashback', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'money', 'admin', NOW(), '返现配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'cashback' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '扣罚', @analysis_config, 4, 'penalty', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'bug', 'admin', NOW(), '扣罚配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'penalty' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '推广费', @analysis_config, 5, 'promotion', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'guide', 'admin', NOW(), '推广费用配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'promotion' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '保证金', @analysis_config, 6, 'margin', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'lock', 'admin', NOW(), '保证金配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'margin' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '回款天数', @analysis_config, 7, 'collectionDays', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'date', 'admin', NOW(), '回款天数配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'collectionDays' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '内部成本', @analysis_config, 8, 'internalCost', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'peoples', 'admin', NOW(), '内部成本配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'internalCost' AND parent_id = @analysis_config);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '仓储成本', @analysis_config, 9, 'warehouseCost', 'operationsManage/config/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'table', 'admin', NOW(), '仓储成本配置'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'warehouseCost' AND parent_id = @analysis_config);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '原生BI', @analysis_root, 3, 'nativeBi', NULL, 1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '经营分析原生BI'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'nativeBi' AND parent_id = @analysis_root);
SET @analysis_bi = (SELECT menu_id FROM sys_menu WHERE path = 'nativeBi' AND parent_id = @analysis_root LIMIT 1);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '拼多多营销竞价', @analysis_bi, 1, 'pddBid', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'chart', 'admin', NOW(), '拼多多营销竞价分析'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'pddBid' AND parent_id = @analysis_bi);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '价保核算明细', @analysis_bi, 2, 'rebateDetail', 'operationsManage/rebate/index', 1, 0, 'C', '0', '0', 'analysis:config:list', 'excel', 'admin', NOW(), '返利价保核算明细'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'rebateDetail' AND parent_id = @analysis_bi);
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '历史经营报表', @analysis_bi, 3, 'historyReport', 'operationsManage/dashboard/index', 1, 0, 'C', '0', '0', 'analysis:dashboard:list', 'documentation', 'admin', NOW(), '经营月报年报'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE path = 'historyReport' AND parent_id = @analysis_bi);

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '核算配置编辑', @analysis_config, 90, '#', NULL, 1, 0, 'B', '0', '0', 'analysis:config:edit', '#', 'admin', NOW(), '核算配置新增修改删除权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'analysis:config:edit');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '核算配置导入', @analysis_config, 91, '#', NULL, 1, 0, 'B', '0', '0', 'analysis:config:import', '#', 'admin', NOW(), '核算配置导入权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'analysis:config:import');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '核算配置导出', @analysis_config, 92, '#', NULL, 1, 0, 'B', '0', '0', 'analysis:config:export', '#', 'admin', NOW(), '核算配置导出权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'analysis:config:export');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营数据同步', @analysis_board, 90, '#', NULL, 1, 0, 'B', '0', '0', 'analysis:sync:run', '#', 'admin', NOW(), '手动同步经营数据权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'analysis:sync:run');
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '经营数据重算', @analysis_board, 91, '#', NULL, 1, 0, 'B', '0', '0', 'analysis:calculate:rebuild', '#', 'admin', NOW(), '重算经营指标权限'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'analysis:calculate:rebuild');

INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT '经营分析每日同步', 'ANALYSIS', 'analysisDailySyncJob.execute()', '0 0 2 * * ?', '3', '1', '0', 'admin', NOW(), '每日02:00同步前一自然日吉客云经营数据'
WHERE NOT EXISTS (SELECT 1 FROM sys_job WHERE invoke_target = 'analysisDailySyncJob.execute()');
