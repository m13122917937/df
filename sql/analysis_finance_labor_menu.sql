-- 经营分析菜单：经营分析 → 资金人力 → 保证金 / 回款天数。
SET @analysis_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '经营分析'
      AND `menu_type` = 'M'
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '经营分析', 0, 6, 'analysis', '#', '', 1, 0,
    'M', '0', '0', '', 'chart', 'admin', NOW(), '经营分析'
WHERE @analysis_menu_id IS NULL;

SET @analysis_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '经营分析'
      AND `menu_type` = 'M'
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '资金人力', @analysis_menu_id, 4, 'finance-labor', '#', '', 1, 0,
    'M', '0', '0', '', 'money', 'admin', NOW(), '经营分析资金与人力成本配置'
WHERE @analysis_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @analysis_menu_id
        AND `path` = 'finance-labor'
        AND `del_flag` = '0'
  );

SET @finance_labor_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `parent_id` = @analysis_menu_id
      AND `path` = 'finance-labor'
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

UPDATE `sys_menu`
SET `parent_id` = @finance_labor_menu_id,
    `menu_name` = '保证金',
    `path` = 'margin',
    `component` = 'operationsManage/config/index',
    `query` = '',
    `perms` = 'analysis:config:list',
    `icon` = 'money',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `component` = 'operationsManage/config/index'
  AND (`path` = 'margin' OR `query` LIKE '%margin%')
  AND @finance_labor_menu_id IS NOT NULL
  AND `del_flag` = '0';

UPDATE `sys_menu`
SET `parent_id` = @finance_labor_menu_id,
    `menu_name` = '回款天数',
    `path` = 'collectionDays',
    `component` = 'operationsManage/config/index',
    `query` = '',
    `perms` = 'analysis:config:list',
    `icon` = 'date',
    `update_by` = 'admin',
    `update_time` = NOW()
WHERE `component` = 'operationsManage/config/index'
  AND (`path` IN ('collectionDays', 'collection-cycle') OR `query` LIKE '%collection%')
  AND @finance_labor_menu_id IS NOT NULL
  AND `del_flag` = '0';

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '保证金', @finance_labor_menu_id, 1, 'margin', 'operationsManage/config/index', '', 1, 0,
    'C', '0', '0', 'analysis:config:list', 'money', 'admin', NOW(), '经营分析保证金配置'
WHERE @finance_labor_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @finance_labor_menu_id
        AND `path` = 'margin'
        AND `del_flag` = '0'
  );

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '回款天数', @finance_labor_menu_id, 2, 'collectionDays', 'operationsManage/config/index', '', 1, 0,
    'C', '0', '0', 'analysis:config:list', 'date', 'admin', NOW(), '经营分析店铺回款天数配置'
WHERE @finance_labor_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @finance_labor_menu_id
        AND `path` = 'collectionDays'
        AND `del_flag` = '0'
  );
