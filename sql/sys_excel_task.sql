-- ----------------------------
-- Table: sys_excel_task - Excel导出任务表
-- 用于记录先生成后下载的导出任务，支持大数据量导出
-- ----------------------------
DROP TABLE IF EXISTS `sys_excel_task`;
CREATE TABLE `sys_excel_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `file_id` varchar(64) DEFAULT NULL COMMENT '文件ID（UUID）',
  `file_name` varchar(255) DEFAULT NULL COMMENT '文件名称',
  `local_path` varchar(500) DEFAULT NULL COMMENT '本地临时文件路径',
  `oss_url` varchar(1000) DEFAULT NULL COMMENT '文件存储OSS URL',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小（字节）',
  `module_name` varchar(100) DEFAULT NULL COMMENT '导出模块名称',
  `query_params` text DEFAULT NULL COMMENT '查询参数JSON',
  `status` tinyint(1) DEFAULT 0 COMMENT '任务状态：0-生成中 1-生成成功 2-生成失败',
  `error_msg` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `downloaded` tinyint(1) DEFAULT 0 COMMENT '是否已下载：0-未下载 1-已下载',
  PRIMARY KEY (`task_id`),
  UNIQUE KEY `uk_file_id` (`file_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Excel导出任务表';
