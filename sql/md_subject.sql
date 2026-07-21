CREATE TABLE IF NOT EXISTS `md_subject` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `subject_code` varchar(64) NOT NULL COMMENT '主体编码',
  `subject_name` varchar(255) NOT NULL COMMENT '主体名称',
  `erp_code` varchar(64) DEFAULT NULL COMMENT 'ERP编号',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `principal_name` varchar(64) DEFAULT NULL COMMENT '负责人',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态：0启用，1停用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_by` bigint DEFAULT NULL,
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` bigint DEFAULT NULL,
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_md_subject_code` (`subject_code`),
  KEY `idx_md_subject_name_status` (`subject_name`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主数据管理-经营主体配置';
