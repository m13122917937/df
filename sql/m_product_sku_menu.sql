-- 将“商品列表”挂载到现有“主数据管理”菜单下。
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
    '商品列表', @master_data_menu_id, 3, 'product', 'master/product/index', '', 1, 0,
    'C', '0', '0', 'master:product:list', 'list', 'admin', NOW(), '商品 SKU 主数据查询'
WHERE @master_data_menu_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM `sys_menu`
      WHERE `parent_id` = @master_data_menu_id
        AND `path` = 'product'
        AND `del_flag` = '0'
  );
