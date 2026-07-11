-- ----------------------------
-- Table: jky_logistics_task - 吉客云物流更新延迟任务表
-- 用于验货成功后延迟5分钟更新吉客云物流信息，重启不丢任务
-- ----------------------------
DROP TABLE IF EXISTS `jky_logistics_task`;
CREATE TABLE `jky_logistics_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_code` varchar(64) NOT NULL COMMENT '订单号',
  `erp_order_id` varchar(64) DEFAULT NULL COMMENT 'ERP订单号',
  `logistics_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `logistics_name` varchar(100) DEFAULT NULL COMMENT '物流公司名称',
  `logistics_code` varchar(64) DEFAULT NULL COMMENT '物流公司编码',
  `status` tinyint(4) DEFAULT 0 COMMENT '状态:0=待处理 1=成功 2=失败',
  `execute_time` datetime NOT NULL COMMENT '计划执行时间',
  `retry_count` int(11) DEFAULT 0 COMMENT '重试次数',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status_time` (`status`, `execute_time`),
  KEY `idx_order_code` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='吉客云物流更新延迟任务';
