-- 仅移除菜单与页面入口，不删除 ana_rebate_* 或 ana_cost_config 中的历史数据。
DELETE FROM sys_menu WHERE path IN ('rebateProtection', 'cashback');
