-- 平台服务费率菜单（挂载到"经营分析"菜单下）
-- 执行日期: 2026-07-27

SET @analysis_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '经营分析'
      AND `menu_type` = 'M'
    ORDER BY `menu_id`
    LIMIT 1
);

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '平台服务费率', @analysis_menu_id, 5, 'platform-fee-rate',
    'operationsManage/platformFeeRate/index', '', 1, 0,
    'C', '0', '0', 'analysis:platformFeeRate:list', 'chart',
    'admin', NOW(), '平台服务费率配置（按平台/业态/品类维度）'
WHERE @analysis_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @analysis_menu_id
        AND `path` = 'platform-fee-rate'
  );
