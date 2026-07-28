-- 将“销售渠道”挂载到现有“主数据管理”菜单下。
SET @master_data_menu_id := (
    SELECT `menu_id`
    FROM `sys_menu`
    WHERE `menu_name` = '主数据管理'
      AND `del_flag` = '0'
    ORDER BY `menu_id`
    LIMIT 1
);

INSERT INTO `sys_menu`
    (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`,
     `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT
    '销售渠道', @master_data_menu_id, 2, 'sales-channel', 'master/sales-channel/index', '', 1, 0,
    'C', '0', '0', 'master:salesChannel:list', 'shop', 'admin', NOW(), '吉客云销售渠道主数据'
WHERE @master_data_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @master_data_menu_id
        AND `path` = 'sales-channel'
        AND `del_flag` = '0'
  );
