-- 移除经营分析中的“产渠分析”和“人效分析”。
-- 保留经营统计、绩效汇总及其使用的人工金额核算；仅删除人效页面独有的人数快照与配置扩展数据。

SET @dashboard_component := 'operationsManage/dashboard/index';

DELETE role_menu
FROM sys_role_menu role_menu
INNER JOIN sys_menu menu ON menu.menu_id = role_menu.menu_id
WHERE menu.path IN ('channelProduction', 'channel-production', 'humanEfficiency', 'human-efficiency')
   OR (menu.component = @dashboard_component
       AND (menu.query LIKE '%channelProduction%' OR menu.query LIKE '%humanEfficiency%'))
   OR menu.parent_id IN (
       SELECT menu_id
       FROM (
           SELECT menu_id
           FROM sys_menu
           WHERE path IN ('channelProduction', 'channel-production', 'humanEfficiency', 'human-efficiency')
              OR (component = @dashboard_component
                  AND (query LIKE '%channelProduction%' OR query LIKE '%humanEfficiency%'))
       ) AS dashboard_menu
   );

DELETE child_menu
FROM sys_menu child_menu
INNER JOIN sys_menu parent_menu ON parent_menu.menu_id = child_menu.parent_id
WHERE parent_menu.path IN ('channelProduction', 'channel-production', 'humanEfficiency', 'human-efficiency')
   OR (parent_menu.component = @dashboard_component
       AND (parent_menu.query LIKE '%channelProduction%' OR parent_menu.query LIKE '%humanEfficiency%'));

DELETE FROM sys_menu
WHERE path IN ('channelProduction', 'channel-production', 'humanEfficiency', 'human-efficiency')
   OR (component = @dashboard_component
       AND (query LIKE '%channelProduction%' OR query LIKE '%humanEfficiency%'));

-- 删除内部成本配置中仅用于人效分析的人员数量扩展字段。
UPDATE ana_cost_config
SET extra_data = JSON_REMOVE(extra_data, '$.headcount')
WHERE config_type = 'INTERNAL_COST'
  AND JSON_VALID(extra_data)
  AND JSON_EXTRACT(extra_data, '$.headcount') IS NOT NULL;

-- 删除每日指标快照中仅用于人效分析的人数列及其历史数据。
SET @has_direct_headcount := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'ana_daily_metric'
      AND column_name = 'direct_headcount'
);
SET @drop_direct_headcount := IF(
    @has_direct_headcount > 0,
    'ALTER TABLE ana_daily_metric DROP COLUMN direct_headcount',
    'SELECT 1'
);
PREPARE statement FROM @drop_direct_headcount;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @has_indirect_headcount := (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'ana_daily_metric'
      AND column_name = 'indirect_headcount'
);
SET @drop_indirect_headcount := IF(
    @has_indirect_headcount > 0,
    'ALTER TABLE ana_daily_metric DROP COLUMN indirect_headcount',
    'SELECT 1'
);
PREPARE statement FROM @drop_indirect_headcount;
EXECUTE statement;
DEALLOCATE PREPARE statement;
