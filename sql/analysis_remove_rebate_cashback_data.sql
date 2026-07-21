-- 已确认清理：先删除返利明细，再删除返利活动与通用配置中的返利/成本返现历史记录。
DELETE FROM `ana_rebate_detail`;
DELETE FROM `ana_rebate_activity`;
DELETE FROM `ana_cost_config` WHERE `config_type` IN ('REBATE', 'CASHBACK');
DROP TABLE IF EXISTS `ana_rebate_detail`;
DROP TABLE IF EXISTS `ana_rebate_activity`;
