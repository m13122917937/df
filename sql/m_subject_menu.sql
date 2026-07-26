-- 将“主体配置”挂载到现有“主数据管理”菜单下；执行前请确认该父菜单唯一存在。
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
    '主体配置', @master_data_menu_id, 1, 'subject', 'master/subject/index', '', 1, 0,
    'C', '0', '0', 'master:subject:list', 'tree-table', 'admin', NOW(), '吉客云经营主体主数据'
WHERE @master_data_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM `sys_menu`
      WHERE `parent_id` = @master_data_menu_id
        AND `path` = 'subject'
        AND `del_flag` = '0'
  );
